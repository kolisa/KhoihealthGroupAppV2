package com.khoihealth.app.core.utils

import kotlin.math.roundToInt

fun Int.toCalories(): String = "$this kcal"
fun Int.toSteps(): String = if (this >= 1000) "${this / 1000}.${(this % 1000) / 100}k" else "$this"
fun Double.toKm(): String = String.format("%.2f km", this)
fun Int.toBpm(): String = "$this bpm"
fun Int.toPercent(): String = "$this%"

fun Float.clamp(min: Float, max: Float): Float = maxOf(min, minOf(max, this))
fun Double.clamp(min: Double, max: Double): Double = maxOf(min, minOf(max, this))

fun Int.metersToKm(): Double = this / 1000.0
fun Int.stepsToKm(strideLength: Double = 0.762): Double = (this * strideLength) / 1000.0
fun Int.stepsToCalories(weightKg: Double = 70.0): Int = (this * 0.04 * (weightKg / 70.0)).roundToInt()

fun Float.progressFraction(goal: Float): Float = (this / goal).clamp(0f, 1f)
fun Int.progressFraction(goal: Int): Float = if (goal == 0) 0f else (this.toFloat() / goal).clamp(0f, 1f)
