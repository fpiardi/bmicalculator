package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import android.graphics.PointF
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.Graph
import com.piardilabs.bmicalculator.MAXIMUM_WEIGHT
import com.piardilabs.bmicalculator.MINIMAL_WEIGHT
import com.piardilabs.bmicalculator.R
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
                .fillMaxWidth()
        )
    }
}

@Composable
fun HistoricalGraphScreen(
    list: List<HistoricalGraphResult>,
    showBackgroundLines: Boolean,
    modifier: Modifier
) {
    val reversedList = list.toMutableList()
    reversedList.reverse()

    val barColors = MaterialTheme.colorScheme.onBackground
    val pathColors = MaterialTheme.colorScheme.primary
    var graphData by remember {
        mutableStateOf(reversedList.toList())
    }

    val legendStyle = TextStyle(
        color = MaterialTheme.colorScheme.secondary,
        fontSize = MaterialTheme.typography.labelSmall.fontSize
    )
    val legendAxisY = stringResource(R.string.measure_weight)
    val animationProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val configuration = LocalConfiguration.current
    val dotRectList = ArrayList<Rect>()
    var normalPathLegend : List<LegendOffsideAxisY>? = null

    LaunchedEffect(key1 = list, key2 = configuration, block = {
        animationProgress.animateTo(1f, tween(2000))
    })

    Box(
        modifier = Modifier
            .padding(8.dp)
            .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
    )
    {
        //Box with graph and background lines
        Box(
            modifier = Modifier
                .padding(start = 48.dp, bottom = 24.dp, top = 24.dp, end = 24.dp)
                //.aspectRatio(16 / 9f)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { tapOffset ->
                            // When the user taps on the Canvas, you can
                            // check if the tap offset is in one of the
                            // tracked Rects.
                            var index = 0
                            for (rect in dotRectList) {
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
                    val dotRectPadding = 16.dp.toPx()
                    dotRectList.clear()

                    val path = generatePath(graphData, size, dotRectList, dotRectPadding)
                    val filledPath = Path()
                    filledPath.addPath(path)
                    filledPath.lineTo(size.width, size.height) //line to bottom right of the graph
                    filledPath.lineTo(0f, size.height) //line to bottom left of the graph
                    filledPath.close()

                    val normalPath = generateNormalPath(graphData, size)
                    normalPathLegend = getLegendOffsideAxisY(graphData, size)

                    val brushPath = Brush.verticalGradient(
                        listOf(
                            pathColors.copy(alpha = 0.85f),
                            pathColors.copy(alpha = 0.40f)
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
                                    radius = 5.dp.toPx(),
                                    style = Fill,
                                    center = Offset(position.x, position.y)
                                )

                                if (item.showValue) {
                                    drawText(
                                        textLayoutResult = textMeasurer.measure(
                                            AnnotatedString(item.weight.toOneDecimal()),
                                            style = legendStyle
                                        ),
                                        topLeft = Offset(
                                            position.x - 12.dp.toPx(),
                                            position.y + 12.dp.toPx()
                                        )
                                    )
                                    drawText(
                                        textLayoutResult = textMeasurer.measure(
                                            AnnotatedString(Date(item.date).formatToViewDateDefaults()),
                                            style = legendStyle
                                        ),
                                        topLeft = Offset(
                                            (size.width / 2) - 40.dp.toPx(),
                                            size.height - 24.dp.toPx()
                                        )
                                    )
                                }
                            }
                        }

                        //draw background grid
                        if (showBackgroundLines) {
                            val barWidthPx = 0.1.dp.toPx()
                            drawRect(color = barColors, style = Stroke(barWidthPx))

                            val verticalLines = list.size
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

        // Box with legends
        Box(
            modifier = Modifier
                .padding(start = 8.dp, bottom = 0.dp, top = 16.dp, end = 8.dp)
                .drawWithCache {
                    onDrawBehind {
                        drawText(
                            textLayoutResult = textMeasurer.measure(
                                AnnotatedString(legendAxisY),
                                style = legendStyle
                            ),
                            topLeft = Offset(0f, 0f)
                        )

                        normalPathLegend?.let {
                            it.forEach { legend ->
                                drawText(
                                    textLayoutResult = textMeasurer.measure(
                                        AnnotatedString(legend.weight.toOneDecimal()),
                                        style = legendStyle
                                    ),
                                    topLeft = legend.position
                                )
                            }
                        }

                    }
                }
        )
    }
}

/**
 * Generate the path of the weight, and calculate the clickable area of the dots
 */
private fun generatePath(
    list: List<HistoricalGraphResult>,
    size: Size,
    dotRectList: ArrayList<Rect>,
    dotSpaces: Float
): Path {

    val path = Path()
    val proportionY = calculateProportionY(list, size)

    list.forEachIndexed { index, item ->
        // positionX will be the size divide by number os measures
        val positionX = size.width / (list.size - 1) * index

        // positionY should be inverted
        val positionY = size.height - ((item.weight - MINIMAL_WEIGHT) * proportionY)
        item.position = PointF(positionX, positionY)

        if (index == 0) {
            item.position = PointF(0f, positionY)
            path.moveTo(x = 0f, y = positionY)
        } else if (index == list.size - 1) {
            item.position = PointF(size.width, positionY)
            path.lineTo(x = size.width, y = positionY)
        } else {
            path.lineTo(x = positionX, y = positionY)
            //to smothie path lines.. change lineTo(x,Y) to use cubicTo function
            //Bezier curve control point
            //path.cubicTo((positionX * 0.6f), (positionY * 0.6f), (positionX * 0.6f), (positionY * 0.6f), positionX, positionY)
        }

        Log.d("fpiardi", "items:${list.size} height:${size.height} proportionY:${proportionY} positionY:${positionY}")

        item.position?.let { position ->
            dotRectList.add(
                Rect(
                    top = validRectValue(position.y - dotSpaces),
                    left = validRectValue(position.x - dotSpaces),
                    bottom = validRectValue(position.y + dotSpaces, size.height),
                    right = validRectValue(position.x + dotSpaces, size.width)
                )
            )
        }

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

/**
 * Use the max weight the user saved and screen size height
 */
private fun calculateProportionY(list: List<HistoricalGraphResult>, size: Size): Float {
    val maxWeight = list.maxOfOrNull { it.weight }?.inc() ?: MAXIMUM_WEIGHT.toFloat()
    return size.height / (maxWeight - MINIMAL_WEIGHT)
}

/**
 * Generate path with the range of normal weight
 */
private fun generateNormalPath(list: List<HistoricalGraphResult>, size: Size): Path {
    val path = Path()
    val proportionY = calculateProportionY(list, size)

    path.moveTo(x = 0f, y = size.height - ((list.first().maxNormalWeight - MINIMAL_WEIGHT) * proportionY))
    path.lineTo(x = size.width, y = size.height - ((list.last().maxNormalWeight - MINIMAL_WEIGHT) * proportionY))
    path.lineTo(x = size.width, y = size.height - ((list.last().minNormalWeight - MINIMAL_WEIGHT) * proportionY))
    path.lineTo(x = 0f, y = size.height - ((list.first().minNormalWeight - MINIMAL_WEIGHT) * proportionY))
    path.close()

    return path
}

private fun getLegendOffsideAxisY(
    list: List<HistoricalGraphResult>,
    size: Size
): List<LegendOffsideAxisY> {
    val response = ArrayList<LegendOffsideAxisY>()
    val proportionY = calculateProportionY(list, size)

    response.add(
        LegendOffsideAxisY(
            Offset(
                0f,
                size.height - ((list.last().maxNormalWeight - MINIMAL_WEIGHT) * proportionY)
            ), weight = list.last().maxNormalWeight
        )
    )
    response.add(
        LegendOffsideAxisY(
            Offset(
                0f,
                size.height - ((list.last().minNormalWeight - MINIMAL_WEIGHT) * proportionY)
            ), weight = list.last().minNormalWeight
        )
    )

    return response
}

data class LegendOffsideAxisY(
    val position: Offset,
    val weight: Float
)