package com.piardilabs.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

enum class Measure {
    HEIGHT,
    WEIGHT
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMICalculatorTheme {
                BMICalculatorApp()
            }
        }
    }
}

fun generateSpinnerValues(minimalValue: Int, maximumValue: Int): List<Int> {
    var list = mutableListOf<Int>()
    for (i in minimalValue..maximumValue) {
        list.add(i)
    }
    return list
}

@Composable
fun TitleAndDescription(title: String, description: String) {
    Column(Modifier.semantics(mergeDescendants = true) {}) {
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
fun FillMetrics(sliderValues: List<Int>, sliderPosition: Float, measure: Measure) {
    //default values for HEIGHT
    var formattedString = "%.0f"
    var calculatedValue = (sliderPosition * sliderValues.size) + sliderValues.first()
    var label = stringResource(R.string.measure_height)

    if (measure == Measure.WEIGHT) {
        formattedString = "%.1f"
        label = stringResource(R.string.measure_weight)
    }

    TitleAndDescription(String.format(formattedString, calculatedValue), label)
}

@Composable
fun customSliderColors(): SliderColors = SliderDefaults.colors(
    activeTickColor = Color.Transparent,
    inactiveTickColor = Color.Transparent,
    inactiveTrackColor = Color.Transparent,
    activeTrackColor = Color.Transparent,
    thumbColor = MaterialTheme.colorScheme.primary,
)

