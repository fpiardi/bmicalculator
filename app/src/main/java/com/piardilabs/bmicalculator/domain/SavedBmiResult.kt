package com.piardilabs.bmicalculator.domain

data class SavedBmiResult(
    val id: Int,
    val date: Long,
    val gender: Int,
    val height: Float,
    val weight: Float,
    val bmi: Float,
    val index: Int,
    val difference: Float
)