package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class EquilibriumRepository(
    private val checkInDao: CheckInDao,
    private val monthlyGoalsDao: MonthlyGoalsDao,
    private val pillarWeightsDao: PillarWeightsDao
) {

    val allCheckIns: Flow<List<CheckInEntity>> = checkInDao.getAllCheckIns()
    val latestCheckIn: Flow<CheckInEntity?> = checkInDao.getLatestCheckIn()
    val recentWeekCheckIns: Flow<List<CheckInEntity>> = checkInDao.getRecentCheckIns(7)
    val monthlyGoals: Flow<MonthlyGoalsEntity?> = monthlyGoalsDao.getMonthlyGoals()

    val pillarWeightsConfig: Flow<PillarWeightsConfig> = pillarWeightsDao.getPillarWeightsConfig()
        .map { entity ->
            if (entity != null) {
                PillarWeightsConfig(
                    workUnderloadImpact = runCatching { ImpactLevel.valueOf(entity.workUnderloadImpactName) }.getOrDefault(ImpactLevel.MODERATE),
                    workOverloadImpact = runCatching { ImpactLevel.valueOf(entity.workOverloadImpactName) }.getOrDefault(ImpactLevel.MODERATE),
                    healthNeglectImpact = runCatching { ImpactLevel.valueOf(entity.healthNeglectImpactName) }.getOrDefault(ImpactLevel.MODERATE),
                    digitalNoiseImpact = runCatching { ImpactLevel.valueOf(entity.digitalNoiseImpactName) }.getOrDefault(ImpactLevel.MODERATE),
                    leisureOverloadImpact = runCatching { ImpactLevel.valueOf(entity.leisureOverloadImpactName) }.getOrDefault(ImpactLevel.MODERATE)
                )
            } else {
                PillarWeightsConfig()
            }
        }

    suspend fun saveWeightsConfig(config: PillarWeightsConfig) {
        pillarWeightsDao.savePillarWeightsConfig(
            PillarWeightsEntity(
                id = 1,
                workUnderloadImpactName = config.workUnderloadImpact.name,
                workOverloadImpactName = config.workOverloadImpact.name,
                healthNeglectImpactName = config.healthNeglectImpact.name,
                digitalNoiseImpactName = config.digitalNoiseImpact.name,
                leisureOverloadImpactName = config.leisureOverloadImpact.name
            )
        )
    }

    suspend fun saveGoals(goals: MonthlyGoalsEntity) {
        monthlyGoalsDao.saveMonthlyGoals(goals)
    }

    suspend fun saveCheckIn(input: DailyCheckInInput, result: HarmonyResult): Long {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())

        val entity = CheckInEntity(
            dateString = todayStr,
            timestamp = System.currentTimeMillis(),
            isDayOff = input.isDayOff,
            workOptionLabel = input.workOption.label,
            sleepOptionLabel = input.sleepOption.label,
            bedtimeLabel = input.bedtimeOption.label,
            wakeTimeLabel = input.wakeTimeOption.label,
            hydrationOptionLabel = input.hydrationOption.label,
            nutritionOptionLabel = input.nutritionOption.label,
            movementOptionLabel = input.movementOption.label,
            medicalExamValid = input.medicalCheckupValid,
            intimacyOptionLabel = input.intimacyOption.label,
            feelingOptionLabel = input.feelingOption.label,
            routineExamsLabel = input.routineExamsOption.label,
            readingOptionLabel = input.readingOption.label,
            leisureOptionLabel = input.leisureOption.label,
            socialOptionLabel = input.socialOption.label,
            creationOptionLabel = input.creationOption.label,
            financeOptionLabel = input.financeOption.label,
            purposeOptionLabel = input.purposeOption.label,
            digitalNoiseOptionLabel = input.digitalNoiseOption.label,
            workScore = result.workScore,
            healthScore = result.healthScore,
            creationScore = result.creationScore,
            financeScore = result.financeScore,
            purposeScore = result.purposeScore,
            readingScore = result.readingScore,
            leisureScore = result.leisureScore,
            socialScore = result.socialScore,
            distancePenaltyD = result.distancePenaltyD,
            digitalNoisePenaltyR = result.digitalNoisePenaltyR,
            harmonyScore = result.harmonyScore,
            statusTitle = result.statusTitle,
            questId = result.microQuest.id,
            questTitle = result.microQuest.title,
            questDescription = result.microQuest.description,
            questTargetPillar = result.microQuest.targetPillar.name,
            questXpReward = result.microQuest.xpReward,
            questCompleted = false
        )

        return checkInDao.insertCheckIn(entity)
    }

    suspend fun setQuestCompleted(id: Long, completed: Boolean) {
        checkInDao.updateQuestStatus(id, completed)
    }

    suspend fun resetAllData() {
        checkInDao.deleteAllCheckIns()
    }

    suspend fun restoreDemoData() {
        checkInDao.deleteAllCheckIns()
        insertSeedItems()
    }

    suspend fun seedInitialDataIfEmpty() {
        val count = checkInDao.countCheckIns()
        if (count == 0) {
            insertSeedItems()
        }
    }

    private suspend fun insertSeedItems() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


            // Create 3 realistic recent days of history demonstrating dynamic harmony
            val seedItems = listOf(
                CheckInEntity(
                    dateString = dateFormat.format(Date(System.currentTimeMillis() - 2 * 86400000L)),
                    timestamp = System.currentTimeMillis() - 2 * 86400000L,
                    isDayOff = false,
                    workOptionLabel = "8h",
                    sleepOptionLabel = "7-8h",
                    hydrationOptionLabel = "Regular",
                    nutritionOptionLabel = "Equilibrada",
                    movementOptionLabel = "30-45m",
                    medicalExamValid = true,
                    readingOptionLabel = "30 min",
                    leisureOptionLabel = "2-3h",
                    socialOptionLabel = "Rotina Compartilhada",
                    digitalNoiseOptionLabel = "<20 min",
                    workScore = 65,
                    healthScore = 75,
                    readingScore = 65,
                    leisureScore = 65,
                    socialScore = 55,
                    distancePenaltyD = 7.0,
                    digitalNoisePenaltyR = 0,
                    harmonyScore = 92,
                    statusTitle = "Harmonia Pura",
                    questId = "seed_1",
                    questTitle = "Sustentar a Harmonia",
                    questDescription = "Celebre o equilíbrio dinâmico e mantenha a consistência sem sobrecargas.",
                    questTargetPillar = "HEALTH",
                    questXpReward = 50,
                    questCompleted = true
                ),
                CheckInEntity(
                    dateString = dateFormat.format(Date(System.currentTimeMillis() - 86400000L)),
                    timestamp = System.currentTimeMillis() - 86400000L,
                    isDayOff = false,
                    workOptionLabel = "10h",
                    sleepOptionLabel = "<6h",
                    hydrationOptionLabel = "Regular",
                    nutritionOptionLabel = "Ultraprocessada",
                    movementOptionLabel = "0m",
                    medicalExamValid = true,
                    readingOptionLabel = "10-15 min",
                    leisureOptionLabel = "1h",
                    socialOptionLabel = "Distante / Isolado",
                    digitalNoiseOptionLabel = "1h-2h",
                    workScore = 85,
                    healthScore = 40,
                    readingScore = 35,
                    leisureScore = 50,
                    socialScore = 15,
                    distancePenaltyD = 23.0,
                    digitalNoisePenaltyR = 15,
                    harmonyScore = 58,
                    statusTitle = "Tensão Moderada",
                    questId = "seed_2",
                    questTitle = "Detox do Sono",
                    questDescription = "Coloque o smartphone em outro cômodo 40 minutos antes de dormir.",
                    questTargetPillar = "HEALTH",
                    questXpReward = 60,
                    questCompleted = false
                )
            )

            for (item in seedItems) {
                checkInDao.insertCheckIn(item)
            }
            monthlyGoalsDao.saveMonthlyGoals(MonthlyGoalsEntity())
        }
}
