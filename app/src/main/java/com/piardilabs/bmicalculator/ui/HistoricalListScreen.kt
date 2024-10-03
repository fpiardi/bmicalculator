package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.piardilabs.bmicalculator.Graph
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.domain.SavedBmiResult
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import com.piardilabs.bmicalculator.utilities.formatToViewDateDefaults
import com.piardilabs.bmicalculator.utilities.toFormattedBmiIndex
import com.piardilabs.bmicalculator.utilities.toOneDecimal
import com.piardilabs.bmicalculator.viewmodel.BmiViewModel
import kotlinx.coroutines.launch
import java.util.Date

@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun HistoricalListPreview() {
    Graph.provide(LocalContext.current)

    val listSavedBmiResult = listOf(
        SavedBmiResult(
            id = 1,
            date = 1676205240,
            gender = 0,
            height = 1.67F,
            weight = 45.9F,
            bmi = 16.4581030F,
            index = 0,
            difference = -5.694647F
        ),
        SavedBmiResult(
            id = 2,
            date = 1677760440,
            gender = 0,
            height = 1.67F,
            weight = 68F,
            bmi = 24.382374F,
            index = 1,
            difference = 0F
        ),
        SavedBmiResult(
            id = 3,
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
        HistoricalListScreen(
            list = listSavedBmiResult,
            bmiViewModel = BmiViewModel(),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun HistoricalListScreen(
    list: List<SavedBmiResult>,
    bmiViewModel: BmiViewModel,
    modifier: Modifier
) {

    val coroutineScope = rememberCoroutineScope()

    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { it.id },
            itemContent = { item ->
                SavedBmiResultItem(item, onRemove = {
                    coroutineScope.launch {
                        bmiViewModel.removeResult(item.id)
                    }
                })
            }
        )
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                modifier = Modifier.weight(0.45F)
            )
            {
                Text(
                    text = Date(item.date).formatToViewDateDefaults(),
                    style = MaterialTheme.typography.titleMedium
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

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End,
                modifier = Modifier.weight(0.55F)
            )
            {
                Text(
                    text = "${item.weight.toOneDecimal()} ${stringResource(R.string.measure_weight)}",
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${resultsText[item.index]} (${item.bmi.toOneDecimal()})",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

    }

}


@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> Color.Transparent
        SwipeToDismissBoxValue.EndToStart -> Color.Red
        SwipeToDismissBoxValue.Settled -> Color.Transparent
    }

    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
            .background(color = color),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            Icons.Default.Delete,
            contentDescription = "delete",
            modifier = Modifier.padding(end = 24.dp)
        )
    }
}

/**
 * Composable representing an bmi result item with swipe-to-dismiss functionality.
 *
 * @param savedBmiResult The saved bmi result to display.
 * @param modifier optional parameters to add extra behaviour to SavedBmiResultItem.
 * @param onRemove Callback invoked when item is dismissed.
 */
@Composable
fun SavedBmiResultItem(
    savedBmiResult: SavedBmiResult,
    modifier: Modifier = Modifier,
    onRemove: (SavedBmiResult) -> Unit
) {
    val currentItem by rememberUpdatedState(savedBmiResult)

    val dismissState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = {
            when(it) {
                SwipeToDismissBoxValue.EndToStart -> {
                    onRemove(currentItem)
                    true
                } else -> {
                    false
                }

            }
        },
        // positional threshold of 25%
        positionalThreshold = { it * .25f }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = { DismissBackground(dismissState)},
        content = {
            SavedBmiItem(savedBmiResult)
        })
}