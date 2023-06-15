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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

enum class Measure {
    HEIGHT,
    WEIGHT
}

class MainActivity : ComponentActivity() {

    val weightSliderValues = generateSpinnerValues(40, 160)
    val heightSliderValues = generateSpinnerValues(120, 240)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var selectedGender by rememberSaveable { mutableStateOf(-1) }
            var sliderWeight by remember { mutableStateOf(0.3f) }

            BMICalculatorTheme {
                // A surface container using the 'background' color from the theme
                Log.d("fpiardi", "Start BMICalculatorTheme")
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChooseGender()
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
        modifier = Modifier.padding(24.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Gender",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please choose your gender to accurate calculations",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row() {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.male_unselected
                ),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight(0.7f)
                    //.align(Alignment.BottomCenter)
                    //.background(Color.Green)
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
                    .fillMaxWidth(0.7f)
                    .fillMaxHeight(0.7f)
                    //.background(Color.Green)
                    .clickable {
                        selectedGender = 1
                    }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))

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

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                enabled = selectedGender >= 0,
                modifier = Modifier.weight(1f, false),
                onClick = { /*TODO*/ }
            ) {
                Text(
                    text = "Next",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                )
            }
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
    Log.d(
        "fpiardi",
        "FillWeight selectedGender=$selectedGender weight=$weight func=$onSliderValueChange"
    )

    Column(
        modifier = Modifier.padding(24.dp).fillMaxHeight(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = "Weight",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please choose your weight using the slider",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        FillMetrics(sliderValues, weight, Measure.WEIGHT)

        Image(
            painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                R.drawable.female_selected
            ),
            contentDescription = "",
            contentScale = ContentScale.FillHeight,
            modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(0.6f)
            //.background(Color.Green)
        )
        Spacer(modifier = Modifier.height(16.dp))

        HorizontalSlider(
            values = sliderValues,
            sliderPosition = weight,
            onSliderValueChange = onSliderValueChange
        )

        Button(
            enabled = selectedGender >= 0,
            modifier = Modifier.weight(1f, false),
            onClick = { /*TODO*/ }
        ) {
            Text(
                text = "Next",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
            )
        }

    }

}

@Composable
private fun FillMetrics(sliderValues: List<Int>, sliderPosition: Float, measure: Measure) {
    Log.d("fpiardi", "FillMetrics sliderPosition=$sliderPosition measure=$measure")

    //default values for HEIGHT
    var formattedString = "%.2f"
    var calculatedValue = ((sliderPosition * sliderValues.size) + sliderValues.first()) / 100
    var label = "cm"

    if (measure == Measure.WEIGHT) {
        formattedString = "%.0f"
        calculatedValue = (sliderPosition * sliderValues.size) + sliderValues.first()
        label = "kg"
    }

    Text(
        text = String.format(formattedString, calculatedValue),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = label,
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.labelSmall
    )
    Spacer(modifier = Modifier.height(24.dp))
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
