package com.example

import com.example.logic.HarmonyCalculator
import com.example.model.*
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testGoldenZoneHarmonyCalculation() {
        val balancedInput = DailyCheckInInput(
            isDayOff = false,
            workOption = WorkOption.H8, // 65 pts
            sleepOption = SleepOption.HOURS_7_8, // 35 pts
            hydrationOption = HydrationOption.REGULAR, // 15 pts
            nutritionOption = NutritionOption.BALANCED, // 15 pts
            movementOption = MovementOption.MIN_30_45, // 15 pts -> health = 80 pts
            readingOption = ReadingOption.MIN_30, // 65 pts
            leisureOption = LeisureOption.H2_3, // 65 pts
            socialOption = SocialOption.ROUTINE, // 55 pts
            digitalNoiseOption = DigitalNoiseOption.LESS_20_MIN // R = 0, Clean Mind buff
        )

        val result = HarmonyCalculator.calculate(balancedInput)

        assertTrue("Health should be balanced and in Golden Zone", result.healthScore in 50..70)
        assertTrue("Harmony score should be in high balance", result.harmonyScore >= 80)
        assertEquals("Harmonia Pura", result.statusTitle)
        assertTrue(result.activeBuffs.any { it.contains("Mente Limpa") })
        assertFalse(result.hasDigitalNoiseFog)
    }

    @Test
    fun testWorkOverloadTriggersCrossPenalties() {
        val overloadInput = DailyCheckInInput(
            isDayOff = false,
            workOption = WorkOption.H12_PLUS, // 100 pts -> overload!
            sleepOption = SleepOption.HOURS_7_8,
            hydrationOption = HydrationOption.REGULAR,
            nutritionOption = NutritionOption.BALANCED,
            movementOption = MovementOption.MIN_30_45,
            readingOption = ReadingOption.MIN_30,
            leisureOption = LeisureOption.H1,
            socialOption = SocialOption.DISTANT,
            digitalNoiseOption = DigitalNoiseOption.HOURS_2_PLUS // 2h+ digital noise
        )

        val result = HarmonyCalculator.calculate(overloadInput)

        assertTrue("Should detect cross penalties", result.crossPenalties.isNotEmpty())
        assertTrue("Should activate digital noise fog", result.hasDigitalNoiseFog)
        assertEquals("Risco de Burnout", result.statusTitle)
        assertNotNull(result.microQuest)
    }

    @Test
    fun testDayOffRestingEquilibrium() {
        val restDayInput = DailyCheckInInput(
            isDayOff = true,
            workOption = WorkOption.H0 // 0h on a rest day is balanced
        )

        val result = HarmonyCalculator.calculate(restDayInput)
        assertEquals("0h on rest day should be 60 pts", 60, result.workScore)
    }

    @Test
    fun testCircadianDisciplineBuff() {
        val circadianInput = DailyCheckInInput(
            bedtimeOption = BedtimeOption.BEFORE_23,
            wakeTimeOption = WakeTimeOption.EARLY_DAWN
        )

        val result = HarmonyCalculator.calculate(circadianInput)
        assertTrue(result.activeBuffs.any { it.contains("Disciplina Circadiana") })
    }

    @Test
    fun testCreationAndFinanceFlowBuffs() {
        val makerInput = DailyCheckInInput(
            creationOption = CreationOption.HOURS_1_2,
            financeOption = FinanceOption.CONTROLLED
        )
        val result = HarmonyCalculator.calculate(makerInput)
        assertTrue(result.activeBuffs.any { it.contains("Estado de Flow") })
        assertTrue(result.activeBuffs.any { it.contains("Tranquilidade Financeira") })
        assertEquals(8, result.pillarDetails.size)
    }

    @Test
    fun testPillarWeightsConfigImpact() {
        val overloadInput = DailyCheckInInput(
            isDayOff = false,
            workOption = WorkOption.H12_PLUS,
            sleepOption = SleepOption.HOURS_7_8,
            hydrationOption = HydrationOption.REGULAR,
            nutritionOption = NutritionOption.BALANCED,
            movementOption = MovementOption.MIN_30_45,
            readingOption = ReadingOption.MIN_30,
            leisureOption = LeisureOption.H1,
            socialOption = SocialOption.DISTANT,
            digitalNoiseOption = DigitalNoiseOption.LESS_20_MIN
        )

        // Moderate vs Extreme work overload weight
        val moderateResult = HarmonyCalculator.calculate(overloadInput, PillarWeightsConfig(workOverloadImpact = ImpactLevel.MODERATE))
        val extremeResult = HarmonyCalculator.calculate(overloadInput, PillarWeightsConfig(workOverloadImpact = ImpactLevel.EXTREME))

        assertTrue("Extreme overload impact should penalize health more severely", extremeResult.healthScore < moderateResult.healthScore)
        assertTrue("Extreme overload impact should reduce harmony score more", extremeResult.harmonyScore < moderateResult.harmonyScore)
    }

    @Test
    fun testAvatarEmotionThresholds() {
        assertEquals(AvatarEmotion.HAPPY, AvatarEmotion.fromScore(95))
        assertEquals(AvatarEmotion.HAPPY, AvatarEmotion.fromScore(80))
        assertEquals(AvatarEmotion.TIRED, AvatarEmotion.fromScore(79))
        assertEquals(AvatarEmotion.TIRED, AvatarEmotion.fromScore(60))
        assertEquals(AvatarEmotion.SAD, AvatarEmotion.fromScore(59))
        assertEquals(AvatarEmotion.SAD, AvatarEmotion.fromScore(40))
        assertEquals(AvatarEmotion.EXHAUSTED, AvatarEmotion.fromScore(39))
        assertEquals(AvatarEmotion.EXHAUSTED, AvatarEmotion.fromScore(0))
    }

    @Test
    fun testPurposeAndPillarSelection() {
        val input = DailyCheckInInput(
            purposeOption = PurposeOption.ALIGNED_ACTIVE
        )
        // With all 8 pillars
        val allResult = HarmonyCalculator.calculate(input)
        assertEquals(8, allResult.pillarDetails.size)
        assertTrue(allResult.activeBuffs.any { it.contains("Alinhamento Existencial") })
        assertEquals(PillarZone.GOLDEN_ZONE, allResult.pillarDetails[PillarType.PURPOSE]?.zone)

        // With only Work and Health (disabling others)
        val filteredPillars = setOf(PillarType.WORK, PillarType.HEALTH)
        val filteredResult = HarmonyCalculator.calculate(input, activePillars = filteredPillars)
        assertEquals(2, filteredResult.activePillars.size)
        assertTrue(filteredResult.pillarDetails[PillarType.WORK]?.isActive == true)
        assertTrue(filteredResult.pillarDetails[PillarType.PURPOSE]?.isActive == false)
    }
}
