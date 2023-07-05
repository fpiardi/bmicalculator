package com.piardilabs.bmicalculator.domain

import androidx.compose.ui.text.font.FontWeight

data class SavedBmiResult(
    val date: Long,
    val gender: Int,
    val height: Float,
    val weight: Float,
    val bmi: Float,
    val index: Int,
    val difference: Float,
//    val minNormalWeight: Float,
//    val maxNormalWeight: Float
)