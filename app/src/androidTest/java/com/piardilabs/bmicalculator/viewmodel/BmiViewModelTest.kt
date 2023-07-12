package com.piardilabs.bmicalculator.viewmodel

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.piardilabs.bmicalculator.data.AppDatabase
import com.piardilabs.bmicalculator.data.BmiResultDao
import com.piardilabs.bmicalculator.data.BmiResultRepository
import com.piardilabs.bmicalculator.domain.BmiResult
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
internal class BmiViewModelTest {

    private lateinit var viewModel: BmiViewModel
    private lateinit var bmiResultDao: BmiResultDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        bmiResultDao = db.bmiResultDao()
        viewModel = BmiViewModel(BmiResultRepository(bmiResultDao))
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }


    @Test
    fun gameViewModel_CalculateUnderWeightBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 16.5F,
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
            bmi = 24.4F,
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
    fun gameViewModel_CalculateNormalWeightBMILimit_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 25F,
            index = 2,
            difference = 0.005388484F,
            minNormalWeight = 51.594646F,
            maxNormalWeight = 69.69461F
        )

        //act
        val result = viewModel.calculateBMI(1.67F, 69.7F)

        //assert
        assertEquals(expectedResult, result)
    }

    @Test
    fun gameViewModel_CalculateOverWeightBMI_ReturnCorrectBMIResult() {
        //arrange
        val expectedResult = BmiResult(
            bmi = 29.5F,
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
            bmi = 29.9F,
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
            bmi = 31.6F,
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
            bmi = 37.6F,
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