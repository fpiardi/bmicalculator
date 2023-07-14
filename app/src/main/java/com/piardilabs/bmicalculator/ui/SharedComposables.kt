package com.piardilabs.bmicalculator.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.R

enum class Measure {
    HEIGHT,
    WEIGHT
}

@Composable
fun TitleAndDescription(title: String, description: String? = null, annotatedString: AnnotatedString? = null) {
    Column(Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(8.dp))

        annotatedString?.let {
            Text(text = it, style = MaterialTheme.typography.labelSmall)
        } ?: kotlin.run {
            if (description != null) {
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
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
