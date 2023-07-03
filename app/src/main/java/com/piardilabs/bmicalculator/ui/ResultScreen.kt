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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.TitleAndDescription
import com.piardilabs.bmicalculator.domain.BmiResult
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import com.piardilabs.bmicalculator.viewmodel.BmiViewModel
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ResultPreview() {
    BMICalculatorTheme {
        ResultScreen(
            bmiViewModel = BmiViewModel(),
            selectedGender = 1, height = 1.64f, weight = 80.5f,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
fun ResultScreen(
    bmiViewModel: BmiViewModel,
    selectedGender: Int,
    height: Float,
    weight: Float,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState()

    val resultsText = stringArrayResource(R.array.results_text)
    val resultsIndex = stringArrayResource(R.array.results_index)
    val resultColors = listOf(
        colorResource(R.color.blue),
        colorResource(R.color.green),
        colorResource(R.color.yellow),
        colorResource(R.color.orange),
        colorResource(R.color.red)
    )

    val bmiResult = bmiViewModel.calculateBMI(height, weight)

    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
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
            pagerState.scrollToPage(bmiResult.index)
        }

        val description = SpannableResult(
            resultText = resultsText[bmiResult.index],
            resultColor = items[bmiResult.index].color,
            bmiResult = bmiResult
        )
        TitleAndDescription(
            title = stringResource(R.string.result_bmi, String.format("%.1f", bmiResult.bmi)),
            annotatedString = description
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
fun SpannableResult(resultText: String, resultColor: Color, bmiResult: BmiResult): AnnotatedString {

    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = resultColor, fontWeight = FontWeight.Bold)) {
            append("$resultText: ")
        }

        val text = stringResource(
            R.string.result_bmi_detail,
            String.format("%.1f", bmiResult.minNormalWeight),
            String.format("%.1f", bmiResult.maxNormalWeight)
        )
        append(text)

        if (bmiResult.index == 0) {
            withStyle(style = SpanStyle(color = resultColor, fontWeight = FontWeight.Bold)) {
                append(
                    " (${
                        String.format(
                            "%.1f",
                            bmiResult.difference
                        )
                    } ${stringResource(R.string.measure_weight)})"
                )
            }
        } else if (bmiResult.index > 1) {
            withStyle(style = SpanStyle(color = resultColor, fontWeight = FontWeight.Bold)) {
                append(
                    " (+${
                        String.format(
                            "%.1f",
                            bmiResult.difference
                        )
                    } ${stringResource(R.string.measure_weight)})"
                )
            }
        } else {
            withStyle(style = SpanStyle(color = resultColor, fontWeight = FontWeight.Bold)) {
                append(" (${stringResource(R.string.result_normal)})")
            }
        }

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
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = resultObject.range,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
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