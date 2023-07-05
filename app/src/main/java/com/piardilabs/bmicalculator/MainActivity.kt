package com.piardilabs.bmicalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.piardilabs.bmicalculator.ui.theme.BMICalculatorTheme
import com.piardilabs.bmicalculator.viewmodel.BmiViewModel
import com.piardilabs.bmicalculator.viewmodel.BmiViewModelFactory

enum class Measure {
    HEIGHT,
    WEIGHT
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bmiViewModel by viewModels<BmiViewModel> { BmiViewModelFactory(applicationContext) }

        setContent {
            BMICalculatorTheme {
                BMICalculatorApp(bmiViewModel)
            }
        }
    }
}
