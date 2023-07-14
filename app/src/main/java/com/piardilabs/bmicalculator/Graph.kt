/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.piardilabs.bmicalculator

import android.content.Context
import com.piardilabs.bmicalculator.data.AppDatabase
import com.piardilabs.bmicalculator.data.BmiResultRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A very simple global singleton dependency graph.
 *
 * For a real app, you would use something like Hilt/Dagger instead.
 */
object Graph {

    private lateinit var database: AppDatabase

    val bmiResultRepository by lazy {
        BmiResultRepository(
            bmiResultDao = database.bmiResultDao()
        )
    }

    private val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main

    private val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO

    fun provide(context: Context) {
        database = AppDatabase.getInstance(context)
    }
}
