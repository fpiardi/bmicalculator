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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.*
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VerticalSliderPreview() {
    BMICalculatorTheme {
        VerticalSlider(values = generateSpinnerValues(130, 200), 0.3f) {}
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChooseHeightScreenPreview() {
    BMICalculatorTheme {
        ChooseHeightScreen(
            selectedGender = 1,
            sliderValues = generateSpinnerValues(130, 200),
            sliderPosition = 0.3f,
            onNextButtonClicked = {},
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        ) { }
    }
}

@Composable
fun ChooseHeightScreen(
    selectedGender: Int,
    sliderValues: List<Int>,
    sliderPosition: Float,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    onSliderValueChange: (Float) -> Unit
) {
    val alreadyChangeSlide = (sliderPosition != DEFAULT_HEIGHT_SLIDER_POSITION)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = stringResource(R.string.height_description),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
        FillMetrics(sliderValues, sliderPosition, Measure.HEIGHT)

        Column(modifier = Modifier.fillMaxHeight(0.9f)) {
            Box {
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .fillMaxHeight(0.7f)
                ) {
                    VerticalSlider(
                        values = sliderValues,
                        sliderPosition = sliderPosition,
                        onSliderValueChange = onSliderValueChange
                    )
                }
            }
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
private fun VerticalSlider(
    values: List<Int>,
    sliderPosition: Float = 0f,
    onSliderValueChange: (Float) -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            //.background(Color.Yellow)
            .fillMaxHeight(1f)
            .fillMaxWidth(1f)
    ) {
        HorizontalLines(values.reversed())

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.End
        ) {
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
                    .graphicsLayer {
                        rotationZ = 270f
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(
                            Constraints(
                                minWidth = constraints.minHeight,
                                maxWidth = constraints.maxHeight,
                                minHeight = constraints.minWidth,
                                maxHeight = constraints.maxWidth,
                            )
                        )
                        layout(placeable.height, placeable.width) {
                            placeable.place(-placeable.width, 0)
                        }
                    }
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),

                colors = customSliderColors(),
                value = sliderPosition,
                onValueChange = { onSliderValueChange(it) }
            )
        }

    }
}

@Composable
private fun HorizontalLines(values: List<Int>) {
    Box(
        modifier = Modifier
            .fillMaxHeight(1f)
            //.background(Color.Cyan)
            .padding(horizontal = 24.dp)
            .width(24.dp)
    ) {
        val drawPadding: Float = with(LocalDensity.current) { 5.dp.toPx() }
        val textPaint = TextPaint()
        textPaint.textSize = MaterialTheme.typography.titleLarge.fontSize.value

        Canvas(modifier = Modifier.fillMaxSize()) {
            val xStart = 0f
            val xEnd = size.width
            val distance: Float = (size.height.minus(1f * drawPadding)).div(values.size.minus(1))
            values.forEachIndexed { index, _ ->
                if (index.rem(5) == 0) {
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(x = xStart, y = drawPadding + index.times(distance)),
                        end = Offset(x = xEnd, y = drawPadding + index.times(distance))
                    )
                }
                if (index.rem(10) == 0) {
                    this.drawContext.canvas.nativeCanvas.drawText(
                        values[index].toString(), // text to be drawn
                        (size.width * 1.15).toFloat(), // x position
                        drawPadding + index.times(distance), // y position
                        textPaint // color, thickness, fontSize, etc
                    )
                }
            }
        }
    }
}