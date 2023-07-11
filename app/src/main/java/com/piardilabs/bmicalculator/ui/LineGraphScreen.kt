package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import android.graphics.PointF
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.Graph
import com.piardilabs.bmicalculator.MAXIMUM_WEIGHT
import com.piardilabs.bmicalculator.MINIMAL_WEIGHT
import com.piardilabs.bmicalculator.domain.HistoricalGraphResult
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import com.piardilabs.bmicalculator.utilities.formatToViewDateDefaults
import com.piardilabs.bmicalculator.utilities.toOneDecimal
import java.util.Date

@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoricalGraphPreview() {
    Graph.provide(LocalContext.current)

    val listSavedBmiResult = listOf(
        HistoricalGraphResult(
            date = 1676205240,
            weight = 45.9F,
            minNormalWeight = 51.59F,
            maxNormalWeight = 69.69F
        ),
        HistoricalGraphResult(
            date = 1677760440,
            weight = 68F,
            minNormalWeight = 51.59F,
            maxNormalWeight = 69.69F
        ),
        HistoricalGraphResult(
            date = 1680352440,
            weight = 82.2F,
            minNormalWeight = 51.59F,
            maxNormalWeight = 69.69F
        )
    )

    BMICalculatorTheme {
        HistoricalGraphScreen(
            list = listSavedBmiResult,
            showBackgroundLines = true,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun HistoricalGraphScreen(
    list: List<HistoricalGraphResult>,
    showBackgroundLines: Boolean,
    modifier: Modifier
) {
    val barColors = MaterialTheme.colorScheme.onBackground
    val pathColors = MaterialTheme.colorScheme.primary
    var graphData by remember {
        mutableStateOf(list)
    }

    val legendStyle = MaterialTheme.typography.labelSmall
    val animationProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val configuration = LocalConfiguration.current
    val dotRects = ArrayList<Rect>()

    LaunchedEffect(key1 = list, key2 = configuration, block = {
        animationProgress.animateTo(1f, tween(2000))
    })

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.inversePrimary)
    )
    {
        Box(
            modifier = Modifier
                .padding(start = 20.dp, bottom = 20.dp, top = 20.dp, end = 20.dp)
                .aspectRatio(16 / 9f)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            // When the user taps on the Canvas, you can
                            // check if the tap offset is in one of the
                            // tracked Rects.
                            var index = 0
                            for (rect in dotRects) {
                                if (rect.contains(tapOffset)) {
                                    graphData = graphData.mapIndexed { i, item ->
                                        if (index == i) {
                                            item.copy(showValue = true)
                                        } else {
                                            item.copy(showValue = false)
                                        }
                                    }
                                    // Handle the click here and do some action based on the index
                                    break // don't need to check other points, so break
                                }
                                index++
                            }
                        }
                    )
                }
                .drawWithCache {
                    val dotSpaces = 8.dp.toPx()
                    dotRects.clear()

                    val path = generatePath(graphData, size, dotRects, dotSpaces)
                    val filledPath = Path()
                    filledPath.addPath(path)
                    filledPath.lineTo(size.width, size.height) //line to bottom right of the graph
                    filledPath.lineTo(0f, size.height) //line to bottom left of the graph
                    filledPath.close()

                    val normalPath = generateNormalPath(graphData, size)

                    val brushPath = Brush.verticalGradient(
                        listOf(
                            pathColors.copy(alpha = 0.75f),
                            Color.Transparent
                        )
                    )

                    val brushNormalRange = Brush.verticalGradient(
                        listOf(
                            Color.Green.copy(alpha = 0.75f),
                            Color.Transparent
                        )
                    )

                    onDrawBehind {
                        // drawPath using clipRect to animate
                        clipRect(right = size.width * animationProgress.value) {
                            drawPath(path = path, color = pathColors, style = Stroke(2.dp.toPx()))
                            drawPath(path = filledPath, brush = brushPath, style = Fill)
                            drawPath(path = normalPath, brush = brushNormalRange, style = Fill)
                        }

                        // drawCircle and text of the points
                        graphData.forEach { item ->
                            item.position?.let { position ->
                                drawCircle(
                                    color = pathColors,
                                    radius = 4.dp.toPx(),
                                    style = Fill,
                                    center = Offset(position.x, position.y)
                                )

                                if (item.showValue) {
                                    drawText(
                                        textLayoutResult = textMeasurer.measure(
                                            AnnotatedString(item.weight.toOneDecimal()),
                                            style = legendStyle
                                        ),
                                        topLeft = Offset(position.x - 12.dp.toPx(), position.y)
                                    )
                                    drawText(
                                        textLayoutResult = textMeasurer.measure(
                                            AnnotatedString(Date(item.date).formatToViewDateDefaults()),
                                            style = legendStyle
                                        ),
                                        topLeft = Offset(position.x - 12.dp.toPx(), size.height)
                                    )
                                }
                            }
                        }

                        //draw background grid
                        if (showBackgroundLines) {
                            val barWidthPx = 0.5.dp.toPx()
                            drawRect(color = barColors, style = Stroke(barWidthPx))

                            val verticalLines = 6
                            val verticalSize = size.width / (verticalLines + 1)
                            repeat(verticalLines) { i ->
                                val startX = verticalSize * (i + 1)
                                drawLine(
                                    color = barColors,
                                    start = Offset(startX, 0f),
                                    end = Offset(startX, size.height),
                                    strokeWidth = barWidthPx
                                )
                            }

                            val horizontalLines = 6
                            val sectionSize = size.height / (horizontalLines + 1)
                            repeat(horizontalLines) { i ->
                                val startY = sectionSize * (i + 1)
                                drawLine(
                                    color = barColors,
                                    start = Offset(0f, startY),
                                    end = Offset(size.width, startY),
                                    strokeWidth = barWidthPx
                                )
                            }
                        }

                    }

                }
        )
    }
}


private fun generatePath(list: List<HistoricalGraphResult>, size: Size, dotRects: ArrayList<Rect>, dotSpaces: Float): Path {
    val path = Path()

    list.forEachIndexed { index, item ->
        // positionX will be the size divide by number os measures
        val positionX = (size.width / list.size) * (index + 1)

        // calculate the weight proportion related to size height
        val proportionY = size.height / (MAXIMUM_WEIGHT - MINIMAL_WEIGHT)

        // positionY should be inverted
        val positionY = size.height - (item.weight * proportionY)
        item.position = PointF(positionX, positionY)

        if (index == 0) {
            item.position = PointF(0f, positionY)
            path.moveTo(x = 0f, y = positionY)
        } else {
            path.lineTo(x = positionX, y = positionY)
            //to smothie path lines.. change lineTo(x,Y) to use cubicTo function
            //Bezier curve control point
            //path.cubicTo((positionX * 0.6f), (positionY * 0.6f), (positionX * 0.6f), (positionY * 0.6f), positionX, positionY)
        }

        item.position?.let { position ->
            dotRects.add(
                Rect(
                    top = validRectValue(position.y - dotSpaces),
                    left = validRectValue(position.x - dotSpaces),
                    bottom = validRectValue(position.y + dotSpaces, size.height),
                    right = validRectValue(position.x + dotSpaces, size.width)
                )
            )
        }


//        path.lineTo(x = positionX, y = positionY)
//        //to smothie path lines.. change lineTo(x,Y) to use cubicTo function

    }

    return path
}

private fun validRectValue(value: Float, maxValue: Float? = null): Float {
    var newValue = value

    if (value < 0) newValue = 0f

    maxValue?.let { maxValue ->
        if (value > maxValue) newValue = maxValue
    }

    return newValue
}

private fun generateNormalPath(list: List<HistoricalGraphResult>, size: Size): Path {
    val path = Path()

    // positionX will be the size divide by number os measures
    val positionX = (size.width / list.size) * list.size

    // calculate the weight proportion related to size height
    val proportionY = size.height / (MAXIMUM_WEIGHT - MINIMAL_WEIGHT)

    // positionY should be inverted
    val positionY = size.height - (list.first().maxNormalWeight * proportionY)

    path.moveTo(x = 0f, y = size.height - list.first().maxNormalWeight * proportionY)
    path.lineTo(x = positionX, y = size.height - list.last().maxNormalWeight * proportionY)
    path.lineTo(x = positionX, y = size.height - list.last().minNormalWeight * proportionY)
    path.lineTo(x = 0f, y = size.height - list.first().minNormalWeight * proportionY)
    path.close()

    return path
}