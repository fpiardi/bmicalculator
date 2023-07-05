package com.piardilabs.bmicalculator.utilities

import java.text.DecimalFormat

fun Any?.isNull() = this == null

fun String.toPriceAmount(): String {
    val dec = DecimalFormat("###,###,###.00")
    return dec.format(this.toDouble())
}

fun Double.toPriceAmount(): String {
    val dec = DecimalFormat("###,###,###.00")
    return dec.format(this)
}

fun Float.toPriceAmount(): String {
    val dec = DecimalFormat("###,###,###.00")
    return dec.format(this)
}

fun Float.toOneDecimal(): String {
    val dec = DecimalFormat("###.0")
    return dec.format(this)
}

/**
 * Specifics for this project
 */
fun Float.toFormattedBmiIndex(index: Int, textForZero: String, textForMeasure: String): String {
    return if (this == 0F) {
        textForZero
    } else {
        val resultValue = this.toOneDecimal()
        if (index == 0) {
            "$resultValue $textForMeasure"
        } else {
            "+$resultValue $textForMeasure"
        }
    }
}
fun Float.toOneDecimalOrTextForZero(textForZero: String): String {
    return if (this == 0F) {
        textForZero
    } else {
        this.toOneDecimal()
    }
}