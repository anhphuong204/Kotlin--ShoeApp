package com.example.shoe

import java.text.NumberFormat
import java.util.Locale

fun Double.toVND(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(this)
}

fun Int.toVND(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(this)
}
