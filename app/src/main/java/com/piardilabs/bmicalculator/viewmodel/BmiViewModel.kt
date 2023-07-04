package com.piardilabs.bmicalculator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piardilabs.bmicalculator.Graph
import com.piardilabs.bmicalculator.data.BmiResultEntity
import com.piardilabs.bmicalculator.data.BmiResultRepository
import com.piardilabs.bmicalculator.domain.BmiResult
import kotlinx.coroutines.withContext
import java.util.Calendar

class BmiViewModel(
    private val bmiResultRepository: BmiResultRepository = Graph.bmiResultRepository
) : ViewModel() {

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
            in 0.0..18.449 -> 0
            in 18.5..24.949 -> 1
            in 25.0..29.949 -> 2
            in 30.0..34.949 -> 3
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

    fun getSavedResults(): List<BmiResultEntity>? {
        return bmiResultRepository.getResults().value
    }

    suspend fun saveResult(
        date: Long = Calendar.getInstance().time.time,
        gender: Int,
        height: Float,
        weight: Float,
        bmi: Float,
        index: Int,
        difference: Float,
        minNormalWeight: Float,
        maxNormalWeight: Float
    ) {
        withContext(viewModelScope.coroutineContext) {
            bmiResultRepository.insert(
                BmiResultEntity(
                    date = date,
                    gender = gender,
                    height = height,
                    weight = weight,
                    bmi = bmi,
                    index = index,
                    difference = difference,
                    minNormalWeight = minNormalWeight,
                    maxNormalWeight = maxNormalWeight
                )
            )
        }
    }

}

