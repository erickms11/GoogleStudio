package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.CheckInEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Equilibrium", appName)
    }

    @Test
    fun testRoomDatabaseInsertAndQuery() = runBlocking {
        val dao = db.checkInDao()
        val entry = CheckInEntity(
            dateString = "2026-09-28",
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
            healthScore = 80,
            readingScore = 65,
            leisureScore = 65,
            socialScore = 55,
            distancePenaltyD = 5.0,
            digitalNoisePenaltyR = 0,
            harmonyScore = 94,
            statusTitle = "Harmonia Pura",
            questId = "q1",
            questTitle = "Manter o Centro",
            questDescription = "Respirar e manter consistência",
            questTargetPillar = "HEALTH",
            questXpReward = 50,
            questCompleted = false
        )

        val id = dao.insertCheckIn(entry)
        val latest = dao.getLatestCheckIn().first()

        assertNotNull(latest)
        assertEquals(94, latest?.harmonyScore)
        assertEquals("Equilibrium", ApplicationProvider.getApplicationContext<Context>().getString(R.string.app_name))

        dao.updateQuestStatus(id, true)
        val updated = dao.getLatestCheckIn().first()
        assertEquals(true, updated?.questCompleted)

        dao.deleteAllCheckIns()
        val countAfterReset = dao.countCheckIns()
        assertEquals(0, countAfterReset)

        val goalsDao = db.monthlyGoalsDao()
        goalsDao.saveMonthlyGoals(
            com.example.data.MonthlyGoalsEntity(
                targetSleepHours = 8.0f,
                targetReadingMinutesDaily = 45
            )
        )
        val savedGoals = goalsDao.getMonthlyGoals().first()
        assertNotNull(savedGoals)
        assertEquals(8.0f, savedGoals?.targetSleepHours)
        assertEquals(45, savedGoals?.targetReadingMinutesDaily)
    }

    @Test
    fun testDataExporterJsonGeneration() {
        val checkIn = CheckInEntity(
            id = 1,
            timestamp = System.currentTimeMillis(),
            dateString = "29/09/2026",
            isDayOff = false,
            workOptionLabel = "8h (Ideal)",
            sleepOptionLabel = "7h a 8h",
            hydrationOptionLabel = "Regular (2L)",
            nutritionOptionLabel = "Equilibrada",
            movementOptionLabel = "30-45 min",
            medicalExamValid = true,
            readingOptionLabel = "30 min",
            leisureOptionLabel = "2h a 3h",
            socialOptionLabel = "Rotina / Família",
            creationOptionLabel = "1h - 2h",
            financeOptionLabel = "Equilibrado",
            digitalNoiseOptionLabel = "< 20 min",
            workScore = 65,
            healthScore = 60,
            creationScore = 65,
            financeScore = 60,
            readingScore = 65,
            leisureScore = 65,
            socialScore = 55,
            distancePenaltyD = 2.5,
            digitalNoisePenaltyR = 0,
            harmonyScore = 85,
            statusTitle = "Harmonia Pura",
            questId = "quest_1",
            questTitle = "Nutrição Intelectual",
            questDescription = "Ler 10 páginas",
            questTargetPillar = "READING",
            questXpReward = 45,
            questCompleted = false
        )

        val goals = com.example.model.MonthlyGoals(
            targetSleepHours = 7.5f,
            targetReadingMinutesDaily = 30,
            targetLeisureHoursDaily = 2.0f,
            targetWorkHoursDaily = 8.0f
        )

        val json = com.example.util.DataExporter.buildJsonExport(listOf(checkIn), goals)
        org.junit.Assert.assertTrue(json.contains("\"app\": \"Equilibrium\""))
        org.junit.Assert.assertTrue(json.contains("\"creationScore\": 65"))
        org.junit.Assert.assertTrue(json.contains("\"financeScore\": 60"))
        org.junit.Assert.assertTrue(json.contains("\"targetReadingMinutesDaily\": 30"))
    }
}
