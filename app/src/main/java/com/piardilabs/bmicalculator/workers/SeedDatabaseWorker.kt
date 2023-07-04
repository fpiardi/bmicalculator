package com.piardilabs.bmicalculator.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.piardilabs.bmicalculator.data.AppDatabase
import com.piardilabs.bmicalculator.data.BmiResultEntity
import com.piardilabs.bmicalculator.utilities.DATABASE_SEED_FILENAME
import kotlinx.coroutines.coroutineScope

class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(DATABASE_SEED_FILENAME).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val bmiResultType = object : TypeToken<List<BmiResultEntity>>() {}.type
                    val resultList: List<BmiResultEntity> = Gson().fromJson(jsonReader, bmiResultType)

                    val database = AppDatabase.getInstance(applicationContext)
                    database.bmiResultDao().insertAll(resultList)

                    Result.success()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
    }
}
