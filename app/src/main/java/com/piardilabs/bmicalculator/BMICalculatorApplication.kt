package com.piardilabs.bmicalculator

import android.app.Application
import com.piardilabs.bmicalculator.data.AppDatabase

class BMICalculatorApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        //AppDatabase.getInstance(this)
        Graph.provide(this)
    }
}