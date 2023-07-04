package com.piardilabs.bmicalculator.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

/**
 * Declaring the column info allows for the renaming of variables without implementing a
 * database migration, as the column name would not change.
 */
@Entity(tableName = "bmi_results")
data class BmiResultEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "gender") val gender: Int,
    @ColumnInfo(name = "height") val height: Float,
    @ColumnInfo(name = "weight") val weight: Float,
    @ColumnInfo(name = "bmi") val bmi: Float,
    @ColumnInfo(name = "index") val index: Int,
    @ColumnInfo(name = "difference") val difference: Float,
    @ColumnInfo(name = "minNormalWeight") val minNormalWeight: Float,
    @ColumnInfo(name = "maxNormalWeight") val maxNormalWeight: Float
)