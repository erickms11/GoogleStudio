package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.CheckInEntity
import com.example.model.MonthlyGoals
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExporter {

    fun buildJsonExport(checkIns: List<CheckInEntity>, goals: MonthlyGoals?): String {
        val root = JSONObject()
        root.put("app", "Equilibrium")
        root.put("description", "Backup completo do ecossistema de harmonia de vida")
        root.put("exportDate", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date()))
        root.put("totalCheckIns", checkIns.size)

        // Monthly goals
        if (goals != null) {
            val goalsJson = JSONObject()
            goalsJson.put("targetSleepHours", goals.targetSleepHours)
            goalsJson.put("targetReadingMinutesDaily", goals.targetReadingMinutesDaily)
            goalsJson.put("targetLeisureHoursDaily", goals.targetLeisureHoursDaily)
            goalsJson.put("targetWorkHoursDaily", goals.targetWorkHoursDaily)
            goalsJson.put("targetWaterLitersDaily", goals.targetWaterLitersDaily)
            goalsJson.put("targetRoutineCheckupDone", goals.targetRoutineCheckupDone)
            root.put("monthlyGoals", goalsJson)
        }

        // CheckIns array
        val checkInsArray = JSONArray()
        for (item in checkIns) {
            val itemJson = JSONObject()
            itemJson.put("id", item.id)
            itemJson.put("dateString", item.dateString)
            itemJson.put("timestamp", item.timestamp)
            itemJson.put("harmonyScore", item.harmonyScore)
            itemJson.put("statusTitle", item.statusTitle)
            itemJson.put("distancePenaltyD", item.distancePenaltyD)
            itemJson.put("digitalNoisePenaltyR", item.digitalNoisePenaltyR)

            // 7 Pillars scores
            val scoresJson = JSONObject()
            scoresJson.put("workScore", item.workScore)
            scoresJson.put("healthScore", item.healthScore)
            scoresJson.put("creationScore", item.creationScore)
            scoresJson.put("financeScore", item.financeScore)
            scoresJson.put("readingScore", item.readingScore)
            scoresJson.put("leisureScore", item.leisureScore)
            scoresJson.put("socialScore", item.socialScore)
            itemJson.put("pillarScores", scoresJson)

            // Daily check-in options & choices
            val choicesJson = JSONObject()
            choicesJson.put("isDayOff", item.isDayOff)
            choicesJson.put("workOption", item.workOptionLabel)
            choicesJson.put("sleepOption", item.sleepOptionLabel)
            choicesJson.put("bedtime", item.bedtimeLabel)
            choicesJson.put("wakeTime", item.wakeTimeLabel)
            choicesJson.put("hydration", item.hydrationOptionLabel)
            choicesJson.put("nutrition", item.nutritionOptionLabel)
            choicesJson.put("movement", item.movementOptionLabel)
            choicesJson.put("medicalExamValid", item.medicalExamValid)
            choicesJson.put("routineExams", item.routineExamsLabel)
            choicesJson.put("intimacy", item.intimacyOptionLabel)
            choicesJson.put("feeling", item.feelingOptionLabel)
            choicesJson.put("creationOption", item.creationOptionLabel)
            choicesJson.put("financeOption", item.financeOptionLabel)
            choicesJson.put("readingOption", item.readingOptionLabel)
            choicesJson.put("leisureOption", item.leisureOptionLabel)
            choicesJson.put("socialOption", item.socialOptionLabel)
            choicesJson.put("digitalNoiseOption", item.digitalNoiseOptionLabel)
            itemJson.put("choices", choicesJson)

            // Quest
            val questJson = JSONObject()
            questJson.put("id", item.questId)
            questJson.put("title", item.questTitle)
            questJson.put("description", item.questDescription)
            questJson.put("targetPillar", item.questTargetPillar)
            questJson.put("xpReward", item.questXpReward)
            questJson.put("completed", item.questCompleted)
            itemJson.put("microQuest", questJson)

            checkInsArray.put(itemJson)
        }

        root.put("checkIns", checkInsArray)

        return root.toString(2)
    }

    fun exportAndShare(context: Context, checkIns: List<CheckInEntity>, goals: MonthlyGoals?) {
        try {
            val jsonContent = buildJsonExport(checkIns, goals)
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) {
                exportDir.mkdirs()
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val file = File(exportDir, "equilibrium_backup_$timeStamp.json")
            file.writeText(jsonContent, Charsets.UTF_8)

            val authority = "${context.packageName}.fileprovider"
            val contentUri: Uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                putExtra(Intent.EXTRA_SUBJECT, "Backup do Equilibrium ($timeStamp)")
                putExtra(Intent.EXTRA_TEXT, "Backup completo dos dados do Equilibrium gerado em $timeStamp.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Exportar Backup do Equilibrium").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
