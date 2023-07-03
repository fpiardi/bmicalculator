package com.piardilabs.bmicalculator.viewmodel

import com.piardilabs.bmicalculator.domain.BmiResult
import org.junit.Assert.assertEquals
import org.junit.Test


internal class BmiViewModelTest {

    private val viewModel = BmiViewModel()

    @Test
    fun gameViewModel_CalculateUnderWeightBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 16.458103F,
            index = 0,
            difference = -5.694647F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 45.9F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateNormalWeightBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 24.382374F,
            index = 1,
            difference = 0F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 68F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateOverWeightBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 29.473986F,
            index = 2,
            difference = 12.505388F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 82.2F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateOverWeightBMIHighLimit_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 29.930101F,
            index = 2,
            difference = 13.286896F,
            minNormalWeight = 49.7576F,
            maxNormalWeight = 67.213104F
        )

        //act
        val result = viewModel.calculateBMI(1.64F, 80.5F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateObesityBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 31.55366F,
            index = 3,
            difference = 18.305391F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 88F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateExtremeObesityBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 37.649254F,
            index = 4,
            difference = 35.305393F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 105F)

        //assert
        assertEquals(expectedResult, result)
    }

}