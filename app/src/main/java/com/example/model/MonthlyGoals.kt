package com.example.model

data class MonthlyGoals(
    val targetSleepHours: Float = 7.5f,
    val targetReadingMinutesDaily: Int = 30,
    val targetLeisureHoursDaily: Float = 2.0f,
    val targetWorkHoursDaily: Float = 8.0f,
    val targetWaterLitersDaily: Float = 2.0f,
    val targetRoutineCheckupDone: Boolean = true
)

data class GoalMetricProgress(
    val title: String,
    val currentFormatted: String,
    val targetFormatted: String,
    val progressPercent: Int,
    val isAchieved: Boolean,
    val pillar: PillarType,
    val motivationalTip: String
)
