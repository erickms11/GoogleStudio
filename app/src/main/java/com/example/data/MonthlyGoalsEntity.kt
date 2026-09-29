package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_goals")
data class MonthlyGoalsEntity(
    @PrimaryKey
    val id: Int = 1,
    val targetSleepHours: Float = 7.5f,
    val targetReadingMinutesDaily: Int = 30,
    val targetLeisureHoursDaily: Float = 2.0f,
    val targetWorkHoursDaily: Float = 8.0f,
    val targetWaterLitersDaily: Float = 2.0f,
    val targetRoutineCheckupDone: Boolean = true
)
