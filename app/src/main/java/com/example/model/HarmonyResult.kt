package com.example.model

data class DailyCheckInInput(
    val isDayOff: Boolean = false,
    val workOption: WorkOption = WorkOption.H8,
    val sleepOption: SleepOption = SleepOption.HOURS_7_8,
    val bedtimeOption: BedtimeOption = BedtimeOption.BETWEEN_23_00,
    val wakeTimeOption: WakeTimeOption = WakeTimeOption.REGULAR,
    val hydrationOption: HydrationOption = HydrationOption.REGULAR,
    val nutritionOption: NutritionOption = NutritionOption.BALANCED,
    val movementOption: MovementOption = MovementOption.MIN_30_45,
    val medicalCheckupValid: Boolean = false,
    val intimacyOption: IntimacyOption = IntimacyOption.BALANCED,
    val feelingOption: FeelingOption = FeelingOption.SERENE,
    val routineExamsOption: RoutineExamsOption = RoutineExamsOption.UP_TO_DATE,
    val readingOption: ReadingOption = ReadingOption.MIN_30,
    val leisureOption: LeisureOption = LeisureOption.H2_3,
    val socialOption: SocialOption = SocialOption.ROUTINE,
    val creationOption: CreationOption = CreationOption.HOURS_1_2,
    val financeOption: FinanceOption = FinanceOption.BALANCED,
    val purposeOption: PurposeOption = PurposeOption.CLEAR_MINDFUL,
    val digitalNoiseOption: DigitalNoiseOption = DigitalNoiseOption.MIN_30_TO_1H
)

data class MicroQuest(
    val id: String,
    val title: String,
    val description: String,
    val targetPillar: PillarType,
    val xpReward: Int = 50,
    val isCompleted: Boolean = false
)

data class PillarScoreDetail(
    val pillar: PillarType,
    val score: Int,
    val zone: PillarZone,
    val crossPenaltyApplied: Int = 0,
    val note: String = "",
    val isActive: Boolean = true
)

data class HarmonyResult(
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
    val statusSubtitle: String,
    val pillarDetails: Map<PillarType, PillarScoreDetail>,
    val activeBuffs: List<String>,
    val crossPenalties: List<String>,
    val microQuest: MicroQuest,
    val hasDigitalNoiseFog: Boolean,
    val activePillars: Set<PillarType> = PillarType.entries.toSet()
)

data class DisciplineMetric(
    val score: Int,
    val sleptEarly: Boolean,
    val wokeEarly: Boolean,
    val bedtimeLabel: String,
    val wakeTimeLabel: String,
    val streakDays: Int,
    val weeklyAdherence: Int,
    val statusLabel: String
)

