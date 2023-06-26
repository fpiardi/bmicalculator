package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.*
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import kotlin.math.roundToInt

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HorizontalSliderPreview() {
    BMICalculatorTheme {
        HorizontalRulerWithSlider(values = generateSpinnerValues(40, 160), 0.3f) {}
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChooseWeightScreenPreview() {
    BMICalculatorTheme {
        ChooseWeightScreen(
            selectedGender = 1,
            sliderValues = generateSpinnerValues(40, 160),
            sliderPosition = 0.17f,
            onNextButtonClicked = {},
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        ) { }
    }
}

@Composable
fun ChooseWeightScreen(
    selectedGender: Int,
    sliderValues: List<Int>,
    sliderPosition: Float,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onSliderValueChange: (Float) -> Unit
) {
    val alreadyChangeSlide = (sliderPosition != DEFAULT_WEIGHT_SLIDER_POSITION)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = stringResource(R.string.weight_description),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
        FillMetrics(sliderValues, sliderPosition, Measure.WEIGHT)

        Column(modifier = Modifier.fillMaxHeight(0.80f)) {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.female_selected
                ),
                contentDescription = null, // decorative
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth(0.80f)
                    .fillMaxHeight(0.70f)
            )
            HorizontalRulerWithSlider(
                values = sliderValues,
                sliderPosition = sliderPosition,
                onSliderValueChange = onSliderValueChange
            )
//            HorizontalRulerWithLazyRow(
//                values = sliderValues,
//                sliderPosition = sliderPosition,
//                onSliderValueChange = onSliderValueChange
//            )
        }

        Button(
            enabled = alreadyChangeSlide,
            onClick = { onNextButtonClicked() }
        ) {
            Text(
                text = stringResource(R.string.text_next),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorizontalRulerWithSlider(
    values: List<Int>,
    sliderPosition: Float = 0f,
    onSliderValueChange: (Float) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        val textRuler = stringResource(R.string.text_ruler)

        VerticalLinesAsRuler(values)
        Slider(
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = MutableInteractionSource(),
                    modifier = Modifier.offset(y = 8.dp),
                    thumbSize = DpSize(8.dp, 80.dp),
                    colors = customSliderColors()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    stateDescription = ((sliderPosition * values.size) + values.first())
                        .roundToInt()
                        .toString()
                    contentDescription = textRuler
                },
            colors = customSliderColors(),
            value = sliderPosition,
            onValueChange = { onSliderValueChange(it) }
        )
    }
}

@Composable
private fun VerticalLinesAsRuler(values: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        val color = MaterialTheme.colorScheme.secondary
        val drawPadding: Float = with(LocalDensity.current) { 6.dp.toPx() }
        val textPaint = TextPaint()
        textPaint.textSize = MaterialTheme.typography.titleLarge.fontSize.value
        textPaint.color = color.toArgb()

        Canvas(modifier = Modifier.fillMaxSize()) {
            val yStart = 0f
            val yEnd = size.height
            val distance: Float = size.width.div(values.size)
            values.forEachIndexed { index, _ ->
                if (index.rem(10) == 0) {
                    drawLine(
                        color = color,
                        start = Offset(x = index.times(distance), y = yStart),
                        end = Offset(
                            x = index.times(distance),
                            y = (size.height * 1.45).toFloat()
                        )
                    )
                } else if (index.rem(2) == 0) {
                    drawLine(
                        color = color,
                        start = Offset(x = index.times(distance), y = yStart),
                        end = Offset(x = index.times(distance), y = yEnd)
                    )
                }

                if (index.rem(20) == 0) {
                    this.drawContext.canvas.nativeCanvas.drawText(
                        values[index].toString(), // text to be drawn
                        (index.times(distance) - drawPadding), // x position
                        (size.height * 1.9).toFloat(), // y position
                        textPaint // color, thickness, fontSize, etc
                    )
                }
            }
        }
    }
}

/**
 * This function will create a ruler that will scroll, and not the slider that will scroll
 */
@Composable
fun HorizontalRulerWithLazyRow(
    values: List<Int>,
    sliderPosition: Float = 0f,
    onSliderValueChange: (Float) -> Unit
) {

    Box(
        modifier = Modifier
            .height(32.dp)
    ) {

        val state = rememberLazyListState()

        LazyRow(horizontalArrangement = Arrangement.SpaceBetween) {
            itemsIndexed(values) { index, item ->
                val drawPadding: Float = with(LocalDensity.current) { 6.dp.toPx() }
                val textPaint = TextPaint()
                textPaint.textSize = MaterialTheme.typography.titleLarge.fontSize.value

                val layoutInfo = state.layoutInfo
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                val itemInfo = visibleItemsInfo.firstOrNull { it.index == index}

                itemInfo?.let {
                    val delta = it.size / 2
                    val center = state.layoutInfo.viewportEndOffset / 2
                    val childCenter = it.offset + it.size / 2
                    val target = childCenter - center
                    if (target in -delta..delta) {
                        onSliderValueChange(item.toFloat())
                    }
                }

                Text(" ")
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val yStart = 0f
                    val yEnd = size.height
                    val distance: Float = (size.width).div(values.size)

                    if (item.rem(5) == 0) {
                        drawLine(
                            color = Color.DarkGray,
                            start = Offset(x = item.times(distance), y = yStart),
                            end = Offset(
                                x = item.times(distance),
                                y = (size.height * 1.45).toFloat()
                            )
                        )
                    } else if (item.rem(1) == 0) {
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = item.times(distance), y = yStart),
                            end = Offset(x = item.times(distance), y = yEnd)
                        )
                    }

                    if (item.rem(10) == 0) {
                        this.drawContext.canvas.nativeCanvas.drawText(
                            (item).toString(), // text to be drawn
                            (item.times(distance) - drawPadding), // x position
                            (size.height * 1.9).toFloat(), // y position
                            textPaint // color, thickness, fontSize, etc
                        )
                    }
                }
            }
        }


    }
}