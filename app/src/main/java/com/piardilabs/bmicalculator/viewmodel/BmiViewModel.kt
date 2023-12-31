package com.piardilabs.bmicalculator.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piardilabs.bmicalculator.Graph
import com.piardilabs.bmicalculator.data.BmiResultEntity
import com.piardilabs.bmicalculator.data.BmiResultRepository
import com.piardilabs.bmicalculator.domain.BmiResult
import com.piardilabs.bmicalculator.utilities.toOneDecimal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.roundToInt

class BmiViewModel(
    private val bmiResultRepository: BmiResultRepository = Graph.bmiResultRepository
) : ViewModel() {

    val savedResults: LiveData<List<BmiResultEntity>> = bmiResultRepository.getResults()

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

        val bmi = (((weight / (height * height)) * 10.0).roundToInt() / 10.0).toFloat()
        val minNormalWeight = 18.5 * (height * height)
        val maxNormalWeight = 24.99 * (height * height)

        val index = when (bmi) {
            in 0.0..18.4 -> 0
            in 18.5..24.9 -> 1
            in 25.0..29.9 -> 2
            in 30.0..34.9 -> 3
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
        //withContext(viewModelScope.coroutineContext) {
        viewModelScope.launch(Dispatchers.IO) {
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

    suspend fun removeResult(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            bmiResultRepository.delete(id)
        }
    }

}

