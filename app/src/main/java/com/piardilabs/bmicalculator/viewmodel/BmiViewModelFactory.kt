package com.piardilabs.bmicalculator.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BmiViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val context = context.applicationContext

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        //return GameViewModel(context) as T
        return BmiViewModel() as T
    }
}
