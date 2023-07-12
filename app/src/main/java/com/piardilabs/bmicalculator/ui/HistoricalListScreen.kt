package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricalListScreen(
    list: List<SavedBmiResult>,
    bmiViewModel: BmiViewModel,
    modifier: Modifier
) {

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { it.id },
            itemContent = { item ->
                val dismissState = rememberDismissState(
                    initialValue = DismissValue.Default,
                    positionalThreshold = { with(density) { 120.dp.toPx() } },
                    confirmValueChange = {
                        when (it) {
                            DismissValue.DismissedToStart -> {
                                // Do Something when swipe End To Start
                                coroutineScope.launch {
                                    bmiViewModel.removeResult(item.id)
                                }
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = { SwipeBackground(dismissState) },
                    dismissContent = { SavedBmiItem(item) }
                )
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
                modifier = Modifier
                    .weight(0.55F)
                    .semantics(mergeDescendants = true) {}
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection ?: return

    val color by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.Default -> Color.LightGray
            DismissValue.DismissedToEnd -> Color.Green
            DismissValue.DismissedToStart -> Color.Red
        }
    )
    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
    }
    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Done
        DismissDirection.EndToStart -> Icons.Default.Delete
    }
    val scale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
    )

    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
            .background(color = color),
        contentAlignment = alignment
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.scale(scale).padding(end = 24.dp)
        )
    }
}