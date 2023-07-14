package com.piardilabs.bmicalculator.domain

import android.graphics.PointF

data class HistoricalGraphResult(
    val date: Long,
    val weight: Float,
    val minNormalWeight: Float,
    val maxNormalWeight: Float,
    var showValue: Boolean = false,
    var position: PointF? = null
)