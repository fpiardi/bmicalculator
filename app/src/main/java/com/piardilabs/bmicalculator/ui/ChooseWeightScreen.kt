package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.*
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HorizontalSliderPreview() {
    BMICalculatorTheme {
        HorizontalSlider(values = generateSpinnerValues(40, 160), 0.3f) {}
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

        Column(modifier = Modifier.fillMaxHeight(0.9f)) {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.female_selected
                ),
                contentDescription = "",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.75f)
            )
            HorizontalSlider(
                values = sliderValues,
                sliderPosition = sliderPosition,
                onSliderValueChange = onSliderValueChange
            )
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
private fun HorizontalSlider(
    values: List<Int>,
    sliderPosition: Float = 0f,
    onSliderValueChange: (Float) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        VerticalLines(values)
        Slider(
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = MutableInteractionSource(),
                    modifier = Modifier.offset(x = 5.dp),
                    thumbSize = DpSize(8.dp, 40.dp),
                    colors = customSliderColors()
                )
            },
            modifier = Modifier
                .semantics { contentDescription = "Localized Description" }
                .fillMaxWidth(),
            colors = customSliderColors(),
            value = sliderPosition,
            onValueChange = { onSliderValueChange(it) }
        )
    }
}

@Composable
private fun VerticalLines(values: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 5.dp.toPx() }
        val textPaint = TextPaint()
        textPaint.textSize = MaterialTheme.typography.titleLarge.fontSize.value

        Canvas(modifier = Modifier.fillMaxSize()) {
            val yStart = 0f
            val yEnd = size.height
            val distance: Float = (size.width.minus(1.9f * drawPadding)).div(values.size.minus(1))
            values.forEachIndexed { index, _ ->
                if (index.rem(5) == 0) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(x = drawPadding + index.times(distance), y = yStart),
                        end = Offset(x = drawPadding + index.times(distance), y = yEnd)
                    )
                }
                if (index.rem(20) == 0) {
                    this.drawContext.canvas.nativeCanvas.drawText(
                        values[index].toString(), // text to be drawn
                        drawPadding + index.times(distance), // x position
                        (size.height * 1.9).toFloat(), // y position
                        textPaint // color, thickness, fontSize, etc
                    )
                }
            }
        }
    }
}