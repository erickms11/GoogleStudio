package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_checkins")
data class CheckInEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isDayOff: Boolean,
    val workOptionLabel: String,
    val sleepOptionLabel: String,
    val bedtimeLabel: String = "Antes das 23h",
    val wakeTimeLabel: String = "5h - 6h30",
    val hydrationOptionLabel: String,
    val nutritionOptionLabel: String,
    val movementOptionLabel: String,
    val medicalExamValid: Boolean,
    val intimacyOptionLabel: String = "Equilibrada",
    val feelingOptionLabel: String = "Sereno",
    val routineExamsLabel: String = "Em Dia",
    val readingOptionLabel: String,
    val leisureOptionLabel: String,
    val socialOptionLabel: String,
    val creationOptionLabel: String = "1h - 2h",
    val financeOptionLabel: String = "Equilibrado",
    val purposeOptionLabel: String = "Consciente & Presente",
    val digitalNoiseOptionLabel: String,
    val workScore: Int,
    val healthScore: Int,
    val creationScore: Int = 60,
    val financeScore: Int = 60,
    val purposeScore: Int = 60,
    val readingScore: Int,
    val leisureScore: Int,
    val socialScore: Int,
    val distancePenaltyD: Double,
    val digitalNoisePenaltyR: Int,
    val harmonyScore: Int,
    val statusTitle: String,
    val questId: String,
    val questTitle: String,
    val questDescription: String,
    val questTargetPillar: String,
    val questXpReward: Int,
    val questCompleted: Boolean = false
)
