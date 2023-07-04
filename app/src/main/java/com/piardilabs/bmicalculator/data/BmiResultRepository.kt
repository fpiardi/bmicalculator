package com.piardilabs.bmicalculator.data

/**
 * Repository module for handling data operations.
 */
class BmiResultRepository constructor(private val bmiResultDao: BmiResultDao) {

    fun getResults() = bmiResultDao.getResults()

    fun getResult(id: Int) = bmiResultDao.getResult(id)

    suspend fun insert(bmiResultEntity: BmiResultEntity) = bmiResultDao.insert(bmiResultEntity)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: BmiResultRepository? = null

        fun getInstance(bmiResultDao: BmiResultDao) =
            instance ?: synchronized(this) {
                instance ?: BmiResultRepository(bmiResultDao).also { instance = it }
            }
    }
}
