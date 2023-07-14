package com.piardilabs.bmicalculator.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.piardilabs.bmicalculator.R
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme

@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview("large font", fontScale = 2f)
@Composable
fun LottieScreenPreview() {
    BMICalculatorTheme {
        LottieScreen(
            lottieSpec = LottieCompositionSpec.RawRes(R.raw.shake_empty_box),
            onAnimationFinished = {}
        )
    }
}

@Composable
fun LottieScreen(
    lottieSpec: LottieCompositionSpec,
    animationSpeed: Float = 1f,
    title: String? = null,
    onAnimationFinished: () -> Unit
) {
    val composition by rememberLottieComposition(spec = lottieSpec)
    val progress by animateLottieCompositionAsState(composition = composition)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
    ) {
        title?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .padding(20.dp)
            )
        }

        LottieAnimation(
            modifier = Modifier.fillMaxSize(),
            composition = composition,
            speed = animationSpeed
        )

        LaunchedEffect(progress) {
            if (progress >= 1) {
                onAnimationFinished()
            }
        }
    }

}
