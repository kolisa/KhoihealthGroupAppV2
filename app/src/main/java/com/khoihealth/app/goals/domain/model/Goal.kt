package com.khoihealth.app.goals.domain.model

data class Goal(
    val dailySteps: Int = 8000,
    val dailyCalories: Int = 500,
    val sleepHours: Float = 8f,
    val activeMinutes: Int = 30,
    val waterMl: Int = 2000
)
