package com.piardilabs.bmicalculator.domain

data class BmiResult(
    val bmi: Float,
    val index: Int,
    val difference: Float,
    val minNormalWeight: Float,
    val maxNormalWeight: Float
)