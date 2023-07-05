package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.domain.SavedBmiResult
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import com.piardilabs.bmicalculator.utilities.formatToViewDateDefaults
import com.piardilabs.bmicalculator.utilities.toFormattedBmiIndex
import com.piardilabs.bmicalculator.utilities.toOneDecimal
import com.piardilabs.bmicalculator.utilities.toOneDecimalOrTextForZero
import com.piardilabs.bmicalculator.utilities.toPriceAmount
import java.text.SimpleDateFormat
import java.util.Date

@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun SavedResultListPreview() {
    val listSavedBmiResult = listOf(
        SavedBmiResult(
            date = 1676205240,
            gender = 0,
            height = 1.67F,
            weight = 45.9F,
            bmi = 16.4581030F,
            index = 0,
            difference = -5.694647F
        ),
        SavedBmiResult(
            date = 1677760440,
            gender = 0,
            height = 1.67F,
            weight = 68F,
            bmi = 24.382374F,
            index = 1,
            difference = 0F
        ),
        SavedBmiResult(
            date = 1680352440,
            gender = 0,
            height = 1.67F,
            weight = 82.2F,
            bmi = 29.473986F,
            index = 2,
            difference = 12.505388F
        )
    )

    BMICalculatorTheme {
        SavedResultListScreen(
            list = listSavedBmiResult,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun SavedResultListScreen(
    list: List<SavedBmiResult>,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier) {
        items(list) {
            SavedBmiItem(it)
        }
    }
}

@Composable
fun SavedBmiItem(item: SavedBmiResult) {
    val resultColors = listOf(
        colorResource(R.color.blue),
        colorResource(R.color.green),
        colorResource(R.color.yellow),
        colorResource(R.color.orange),
        colorResource(R.color.red)
    )
    val resultsText = stringArrayResource(R.array.results_text)

    Card(
        Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = (resultColors[item.index]),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .semantics(mergeDescendants = true) {}
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(0.55F)
                    .semantics(mergeDescendants = true) {}
            )
            {
                Text(
                    text = item.bmi.toOneDecimal(),
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = resultsText[item.index],
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.semantics(mergeDescendants = true) {}
            )
            {
                Text(
                    text = Date(item.date).formatToViewDateDefaults(),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.difference.toFormattedBmiIndex(
                        index = item.index,
                        textForZero = stringResource(R.string.result_normal),
                        textForMeasure = stringResource(R.string.measure_weight)
                    ),
                    style = MaterialTheme.typography.headlineLarge
                )
            }
        }

    }

}