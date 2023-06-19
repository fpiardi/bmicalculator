package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.TitleAndDescription
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResultPreview() {
    BMICalculatorTheme {
        ResultScreen(
            selectedGender = 0, height = 1.67f, weight = 70f,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun ResultScreen(
    selectedGender: Int,
    height: Float,
    weight: Float,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState()
    val bmi = weight / (height * height)

    val resultsText = stringArrayResource(R.array.results_text)
    val resultsIndex = stringArrayResource(R.array.results_index)
    val resultColors = listOf(
        colorResource(R.color.blue),
        colorResource(R.color.green),
        colorResource(R.color.yellow),
        colorResource(R.color.orange),
        colorResource(R.color.red)
    )

    val index = when (bmi) {
        in 0.0..18.4 -> 0
        in 18.5..24.9 -> 1
        in 25.0..29.9 -> 2
        in 30.0..24.9 -> 3
        else -> 4
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {

        val items = mutableListOf<ResultObject>()
        resultsText.forEachIndexed { index, s ->
            items.add(ResultObject(s, resultsIndex[index], resultColors[index]))
        }

        //scroll to page
        val coroutineScope = rememberCoroutineScope()
        coroutineScope.launch {
            // Call scroll to on pagerState
            pagerState.scrollToPage(index)
        }

        TitleAndDescription(
            title = stringResource(R.string.result_bmi, String.format("%.1f", bmi)),
            description = resultsText[index]
        )

        BMIResultsPager(
            selectedGender = selectedGender,
            items = items,
            pagerState = pagerState,
            modifier = Modifier
                .padding(start = 0.dp, top = 0.dp, end = 0.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
        )

        Text(
            text = stringResource(R.string.disclaimer),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelSmall
        )
    }

}

@Composable
fun BMIResultsPager(
    items: List<ResultObject>,
    pagerState: PagerState,
    selectedGender: Int,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        count = items.size,
        state = pagerState,
        contentPadding = PaddingValues(start = 32.dp, end = 32.dp),
        modifier = modifier
    ) { page ->

        val item = items[page]
        ResultCarouselItem(
            selectedGender = selectedGender,
            resultObject = item,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun ResultCarouselItem(
    modifier: Modifier = Modifier,
    selectedGender: Int,
    resultObject: ResultObject
) {
    Card(
        modifier.padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(0.8f)
        ) {
            Image(
                painter = if (selectedGender == 0) painterResource(R.drawable.male_selected) else painterResource(
                    R.drawable.female_selected
                ),
                colorFilter = ColorFilter.tint(resultObject.color),
                contentDescription = "",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(1f)
            )
        }

        Column(
            Modifier
                .fillMaxWidth()
                .background(resultObject.color)
        ) {
            Text(
                text = resultObject.text,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = resultObject.range,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

data class ResultObject(
    val text: String,
    val range: String,
    val color: Color
)