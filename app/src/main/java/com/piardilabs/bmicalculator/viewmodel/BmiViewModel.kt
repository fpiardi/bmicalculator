package com.piardilabs.bmicalculator.viewmodel

import androidx.lifecycle.ViewModel
import com.piardilabs.bmicalculator.domain.BmiResult

class BmiViewModel : ViewModel() {

    fun generateSpinnerValues(minimalValue: Int, maximumValue: Int): List<Int> {
        var list = mutableListOf<Int>()
        for (i in minimalValue..maximumValue) {
            list.add(i)
        }
        return list
    }

    fun calculateBMI(
        height: Float,
        weight: Float
    ): BmiResult {

        val bmi = weight / (height * height) //27.889
        val minNormalWeight = 18.5 * (height * height)
        val maxNormalWeight = 24.99 * (height * height)

        val index = when (bmi) {
            in 0.0..18.49 -> 0
            in 18.5..24.99 -> 1
            in 25.0..29.99 -> 2
            in 30.0..34.99 -> 3
            else -> 4
        }

        val difference = if (index == 0) {
            (weight - minNormalWeight).toFloat()
        } else if (index > 1) {
            (weight - maxNormalWeight).toFloat()
        } else {
            0F
        }

        return BmiResult(
            bmi = bmi,
            index = index,
            difference = difference,
            minNormalWeight = minNormalWeight.toFloat(),
            maxNormalWeight = maxNormalWeight.toFloat()
        )
    }


}

