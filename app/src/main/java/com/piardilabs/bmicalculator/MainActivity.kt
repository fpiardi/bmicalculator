package com.piardilabs.bmicalculator

import android.content.res.Configuration
import android.os.Bundle
import android.text.TextPaint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

enum class Measure {
    HEIGHT,
    WEIGHT
}

class MainActivity : ComponentActivity() {

    private val weightSliderValues = generateSpinnerValues(40, 160)
    private val heightSliderValues = generateSpinnerValues(130, 200)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var selectedGender by rememberSaveable { mutableStateOf(-1) }
            var sliderHeight by rememberSaveable { mutableStateOf(0.3f) }
            var sliderWeight by rememberSaveable { mutableStateOf(0.3f) }

            BMICalculatorTheme {
                // A surface container using the 'background' color from the theme
                Log.d("fpiardi", "Start BMICalculatorTheme")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    ChooseGender()

                    FillHeight(
                        sliderValues = heightSliderValues,
                        selectedGender = selectedGender,
                        height = sliderHeight
                    ) { sliderHeight = it }

//                    FillWeight(
//                        sliderValues = weightSliderValues,
//                        selectedGender = selectedGender,
//                        weight = sliderWeight
//                    ) { sliderWeight = it }

                }
            }
        }

    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HorizontalSliderPreview() {
    BMICalculatorTheme {
        HorizontalSlider(values = generateSpinnerValues(150, 200), 0.3f) {}
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChooseGenderPreview() {
    BMICalculatorTheme {
        ChooseGender()
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FillHeightPreview() {
    BMICalculatorTheme {
        FillHeight(
            sliderValues = generateSpinnerValues(130, 200),
            selectedGender = 1,
            height = 0.0f
        ) { }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FillWeightPreview() {
    BMICalculatorTheme {
        FillWeight(
            sliderValues = generateSpinnerValues(40, 160),
            selectedGender = 1,
            weight = 0.17f
        ) { }
    }
}

private fun generateSpinnerValues(minimalValue: Int, maximumValue: Int): List<Int> {
    var list = mutableListOf<Int>()
    for (i in minimalValue..maximumValue) {
        list.add(i)
    }
    return list
}

@Composable
fun ChooseGender() {
    var selectedGender by rememberSaveable { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        TitleAndDescription(title = "Gender", description = "Please choose your gender to accurate calculations" )

        Row() {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.male_unselected
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .fillMaxHeight(0.7f)
                    .clickable {
                        selectedGender = 0
                    }
            )
            Image(
                painter = if (selectedGender == 1) painterResource(R.drawable.female_selected) else painterResource(
                    R.drawable.female_unselected
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .clickable {
                        selectedGender = 1
                    }
            )
        }

        Row() {
            Text(
                text = "Male", Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = if (selectedGender == 0) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Female", Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = if (selectedGender == 1) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
            )
        }

        Button(
            enabled = selectedGender >= 0,
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Next",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }

}


@Composable
fun FillHeight(
    sliderValues: List<Int>,
    selectedGender: Int,
    height: Float,
    onSliderValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {
        TitleAndDescription("Height", "Please choose your height using the slider")
        FillMetrics(sliderValues, height, Measure.HEIGHT)

        Box {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.female_selected
                ),
                contentDescription = "",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.7f)
                //.background(Color.Green)
            )
            Column(
                modifier = Modifier
                    //.background(Color.Green)
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.7f)
            ) {
                VerticalSlider(
                    values = sliderValues,
                    sliderPosition = height,
                    onSliderValueChange = onSliderValueChange
                )
            }
        }

        Button(
            enabled = selectedGender >= 0,
            //modifier = Modifier.weight(1f, false),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Next",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }
}

/**
 * An example for customize the slider https://piotrprus.medium.com/custom-slider-in-jetpack-compose-43ed08e2c338
 */
@Composable
fun FillWeight(
    sliderValues: List<Int>,
    selectedGender: Int,
    weight: Float,
    onSliderValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        TitleAndDescription("Weight", "Please choose your weight using the slider")
        FillMetrics(sliderValues, weight, Measure.WEIGHT)

        Column {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.female_selected
                ),
                contentDescription = "",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.7f)
                //.background(Color.Green)
            )
            HorizontalSlider(
                values = sliderValues,
                sliderPosition = weight,
                onSliderValueChange = onSliderValueChange
            )
        }

        Button(
            enabled = selectedGender >= 0,
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Next",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }

}

@Composable
private fun TitleAndDescription(title: String, description: String) {
    Column() {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FillMetrics(sliderValues: List<Int>, sliderPosition: Float, measure: Measure) {
    //default values for HEIGHT
    var formattedString = "%.0f"
    //var calculatedValue = (sliderPosition * sliderValues.size) + sliderValues.last()
    var calculatedValue = (sliderPosition * sliderValues.size) + sliderValues.first()
    var label = "cm"

    if (measure == Measure.WEIGHT) {
        formattedString = "%.1f"
        label = "kg"
    }

    TitleAndDescription(String.format(formattedString, calculatedValue), label)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalSlider(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalSlider(
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
private fun customSliderColors(): SliderColors = SliderDefaults.colors(
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
    inactiveTrackColor = Color.LightGray,
    activeTrackColor = Color.LightGray,
    thumbColor = Color.Green,
)

@Composable
fun HorizontalLines(values: List<Int>) {
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

@Composable
fun VerticalLines(values: List<Int>) {
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
                        //(size.width * 1.9).toFloat(), // x position
                        (size.height * 1.9).toFloat(), // y position
                        textPaint // color, thickness, fontSize, etc
                    )
                }
            }
        }
    }
}


@Composable
fun MessageCard() {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.female_selected),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .background(Color.Green)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "Author",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(shadowElevation = 1.dp) {
                Text(
                    text = "Body",
                    modifier = Modifier.padding(all = 4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
