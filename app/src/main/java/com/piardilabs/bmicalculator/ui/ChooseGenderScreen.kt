package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

@Preview(showBackground = true)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun ChooseGenderPreview() {
    BMICalculatorTheme {
        ChooseGenderScreen(
            selectedGender = -1,
            onGenderSelected = {},
            onNextButtonClicked = {},
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun ChooseGenderScreen(
    selectedGender: Int,
    onGenderSelected: (Int) -> Unit,
    onNextButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGender by rememberSaveable { mutableStateOf(selectedGender) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        Text(
            text = stringResource(R.string.gender_description),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )

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
                        onGenderSelected(selectedGender)
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
                        onGenderSelected(selectedGender)
                    }
            )
        }

        Row() {
            Text(
                text = stringResource(R.string.gender_male), Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = if (selectedGender == 0) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
            )
            Text(
                text = stringResource(R.string.gender_female), Modifier.weight(0.5f),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.secondary,
                style = if (selectedGender == 1) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
            )
        }

        Button(
            enabled = selectedGender >= 0,
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