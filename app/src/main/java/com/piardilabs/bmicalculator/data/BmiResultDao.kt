package com.piardilabs.bmicalculator.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BmiResultDao {
    @Query("SELECT * FROM bmi_results ORDER BY date DESC")
    fun getResults(): LiveData<List<BmiResultEntity>>

    @Query("SELECT * FROM bmi_results WHERE id = :id")
    fun getResult(id: Int): LiveData<BmiResultEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(results: List<BmiResultEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bmiResultEntity: BmiResultEntity)
}
