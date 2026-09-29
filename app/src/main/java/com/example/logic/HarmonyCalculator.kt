package com.example.logic

import com.example.model.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object HarmonyCalculator {

    fun calculate(
        input: DailyCheckInInput,
        weights: PillarWeightsConfig = PillarWeightsConfig(),
        activePillars: Set<PillarType> = PillarType.entries.toSet()
    ): HarmonyResult {
        val crossPenalties = mutableListOf<String>()
        val activeBuffs = mutableListOf<String>()

        // 1. Calculate raw work score (considering underload impact)
        var rawWork = when (input.workOption) {
            WorkOption.H0 -> if (input.isDayOff) 60 else {
                val score = (20.0 / weights.workUnderloadImpact.multiplier).roundToInt().coerceIn(5, 30)
                if (weights.workUnderloadImpact != ImpactLevel.MODERATE) {
                    crossPenalties.add("Sub-Trabalho (0h • Peso ${weights.workUnderloadImpact.label} ${weights.workUnderloadImpact.multiplier}x): Defasagem produtiva acentuada")
                }
                score
            }
            WorkOption.H4 -> if (input.isDayOff) 55 else {
                val score = (40.0 - (10.0 * (weights.workUnderloadImpact.multiplier - 1.0))).roundToInt().coerceIn(15, 45)
                if (weights.workUnderloadImpact == ImpactLevel.STRICT || weights.workUnderloadImpact == ImpactLevel.EXTREME) {
                    crossPenalties.add("Sub-Trabalho (4h • Peso ${weights.workUnderloadImpact.label}): Abaixo da dedicação planejada")
                }
                score
            }
            WorkOption.H6 -> 55
            WorkOption.H8 -> 65
            WorkOption.H10 -> 85
            WorkOption.H12_PLUS -> 100
        }

        // 2. Calculate raw health score with neglect sensitivity
        val sleepPts = input.sleepOption.points
        val hydrationPts = input.hydrationOption.points
        val nutritionPts = input.nutritionOption.points
        val movementPts = input.movementOption.points
        var rawHealth = sleepPts + hydrationPts + nutritionPts + movementPts
        var healthPenalty = 0

        // Neglect multiplier for health deficiencies
        if (rawHealth < 50 && weights.healthNeglectImpact != ImpactLevel.MODERATE) {
            val neglectExtraPenalty = ((50 - rawHealth) * (weights.healthNeglectImpact.multiplier - 1.0)).roundToInt()
            rawHealth = (rawHealth - neglectExtraPenalty).coerceIn(0, 100)
            if (weights.healthNeglectImpact == ImpactLevel.STRICT || weights.healthNeglectImpact == ImpactLevel.EXTREME) {
                crossPenalties.add("Custo de Negligência da Saúde (Peso ${weights.healthNeglectImpact.label} ${weights.healthNeglectImpact.multiplier}x): Desgaste biológico acelerado")
            }
        }

        // Disciplina Circadiana (Dormir Cedo & Acordar Cedo)
        val isCircadianMaster = (input.bedtimeOption == BedtimeOption.BEFORE_23 && input.wakeTimeOption == WakeTimeOption.EARLY_DAWN)
        if (isCircadianMaster) {
            activeBuffs.add("Disciplina Circadiana: Dormir & Acordar Cedo (+5 pts Vigor & Foco)")
            rawHealth = min(100, rawHealth + 5)
        } else if (input.bedtimeOption == BedtimeOption.AFTER_00) {
            crossPenalties.add("Dormir Tarde (${input.bedtimeOption.label}): Descompasso circadiano (-5 pts em Saúde)")
        }

        // Saúde Íntima & Sexual
        when (input.intimacyOption) {
            IntimacyOption.FULFILLING -> {
                activeBuffs.add("Vitalidade Íntima: Conexão plena e equilíbrio hormonal")
                rawHealth = min(100, rawHealth + 3)
            }
            IntimacyOption.LOW_ENERGY -> {
                crossPenalties.add("Fadiga Íntima: Alerta de sobrecarga ou estresse corporal")
            }
            else -> {}
        }

        // Estado Emocional & Sentimentos
        when (input.feelingOption) {
            FeelingOption.SERENE -> activeBuffs.add("Mente Serena: Equilíbrio emocional positivo")
            FeelingOption.FOCUSED -> activeBuffs.add("Foco Claro: Alta energia produtiva")
            FeelingOption.OVERWHELMED -> {
                crossPenalties.add("Sobrecarga Emocional: Ansiedade e tensão acumulada (-5 pts Saúde)")
            }
            FeelingOption.EXHAUSTED -> {
                crossPenalties.add("Esgotamento Emocional: Sinal crítico de necessidade de repouso (-8 pts Saúde)")
            }
        }

        // Exames de Rotina & Prevenção
        if (input.routineExamsOption == RoutineExamsOption.UP_TO_DATE || input.medicalCheckupValid) {
            activeBuffs.add("Escudo Médico: Exames de rotina em dia (proteção anti-decaimento)")
        } else if (input.routineExamsOption == RoutineExamsOption.PENDING) {
            crossPenalties.add("Saúde Preventiva: Exames de rotina pendentes há mais de 12 meses")
        }

        // 3. Calculate raw creation score (Maker, Robótica, Gamedev, Pintura)
        var rawCreation = input.creationOption.points
        if (input.creationOption == CreationOption.HOURS_1_2 || input.creationOption == CreationOption.HOURS_3_PLUS) {
            activeBuffs.add("Estado de Flow: Robótica, gamedev e arte ativam inovação e foco criativo")
        }

        // 4. Calculate raw finance score (Finanças & Prosperidade)
        var rawFinance = input.financeOption.points
        if (input.financeOption == FinanceOption.CONTROLLED) {
            activeBuffs.add("Tranquilidade Financeira: Aporte concluído e orçamento blindado")
        } else if (input.financeOption == FinanceOption.IMPULSIVE) {
            crossPenalties.add("Tensão Financeira: Gastos impulsivos geram estresse (-5 pts em Saúde)")
            healthPenalty += 5
        }

        // 5. Calculate raw reading score
        var rawReading = input.readingOption.points

        // 6. Calculate raw leisure score
        var rawLeisure = input.leisureOption.points

        // 7. Calculate raw social score
        var rawSocial = input.socialOption.points

        // 8. Cross-penalties from Work Overload (weighted)
        var socialPenalty = 0
        if (rawWork >= 85) {
            val basePenalty = if (rawWork >= 100) 15 else 10
            val penalty = (basePenalty * weights.workOverloadImpact.multiplier).roundToInt()
            healthPenalty += penalty
            socialPenalty += penalty
            crossPenalties.add("Sobrecarga de Trabalho (${input.workOption.label} • Peso ${weights.workOverloadImpact.label} ${weights.workOverloadImpact.multiplier}x): -$penalty pts em Saúde e -$penalty pts em Relações")
        }

        // 9. Cross-penalties from Leisure Overload (weighted)
        if (rawLeisure >= 90 && !input.isDayOff) {
            val leisurePen = (10 * weights.leisureOverloadImpact.multiplier).roundToInt()
            rawWork = max(5, rawWork - leisurePen)
            crossPenalties.add("Excesso de Lazer (${input.leisureOption.label} • Peso ${weights.leisureOverloadImpact.label}): Canibalizou -$leisurePen pts de Trabalho")
        }

        // 10. Digital Noise Modifier (weighted)
        val digitalNoiseR = (input.digitalNoiseOption.penaltyR * weights.digitalNoiseImpact.multiplier).roundToInt()
        var readingPenalty = 0
        val hasNoiseFog = input.digitalNoiseOption == DigitalNoiseOption.HOUR_1_TO_2 ||
                input.digitalNoiseOption == DigitalNoiseOption.HOURS_2_PLUS

        when (input.digitalNoiseOption) {
            DigitalNoiseOption.LESS_20_MIN -> {
                activeBuffs.add("Buff Mente Limpa: Ruído <20min (+15% XP de Harmonia)")
            }
            DigitalNoiseOption.MIN_30_TO_1H -> {
                // Neutral
            }
            DigitalNoiseOption.HOUR_1_TO_2 -> {
                val rPen = (10 * weights.digitalNoiseImpact.multiplier).roundToInt()
                val hPen = (5 * weights.digitalNoiseImpact.multiplier).roundToInt()
                readingPenalty += rPen
                healthPenalty += hPen
                crossPenalties.add("Ruído Digital (1h-2h • Peso ${weights.digitalNoiseImpact.label}): -$rPen pts Leitura, -$hPen pts Saúde")
            }
            DigitalNoiseOption.HOURS_2_PLUS -> {
                val rPen = (20 * weights.digitalNoiseImpact.multiplier).roundToInt()
                val hPen = (15 * weights.digitalNoiseImpact.multiplier).roundToInt()
                readingPenalty += rPen
                healthPenalty += hPen
                crossPenalties.add("Ruído Digital Severo (2h+ • Peso ${weights.digitalNoiseImpact.label}): -$rPen pts Leitura, -$hPen pts Saúde, névoa visual")
            }
        }

        // Apply health penalty with medical checkup buffer
        var finalHealth = rawHealth - healthPenalty
        if (input.medicalCheckupValid && finalHealth < 50) {
            finalHealth = 50 // Guaranteed floor protection
        }
        finalHealth = finalHealth.coerceIn(0, 100)

        val finalReading = (rawReading - readingPenalty).coerceIn(0, 100)
        val finalSocial = (rawSocial - socialPenalty).coerceIn(0, 100)
        val finalWork = rawWork.coerceIn(0, 100)
        val finalLeisure = rawLeisure.coerceIn(0, 100)
        val finalCreation = rawCreation.coerceIn(0, 100)
        val finalFinance = rawFinance.coerceIn(0, 100)

        // 7.1 Calculate raw purpose score (Propósito & Existencialismo)
        var rawPurpose = input.purposeOption.points
        if (input.purposeOption == PurposeOption.ALIGNED_ACTIVE) {
            activeBuffs.add("Alinhamento Existencial: Ações conscientes alinhadas ao propósito de vida (+5 pts)")
            rawPurpose = min(100, rawPurpose + 5)
        } else if (input.purposeOption == PurposeOption.LOST_QUESTIONING) {
            crossPenalties.add("Desconexão Existencial: Sensação de vazio de sentido ou piloto automático")
        }
        val finalPurpose = rawPurpose.coerceIn(0, 100)

        // 11. Distance penalty D from Target = 60 across ACTIVE pillars with custom weights
        val effectiveActivePillars = if (activePillars.size >= 2) activePillars else PillarType.entries.toSet()

        val allScores = mapOf(
            PillarType.WORK to finalWork,
            PillarType.HEALTH to finalHealth,
            PillarType.CREATION to finalCreation,
            PillarType.FINANCE to finalFinance,
            PillarType.READING to finalReading,
            PillarType.LEISURE to finalLeisure,
            PillarType.RELATIONS to finalSocial,
            PillarType.PURPOSE to finalPurpose
        )

        val target = 60.0
        var sumAbsDiff = 0.0
        effectiveActivePillars.forEach { pillar ->
            val score = allScores[pillar] ?: 60
            val diff = when (pillar) {
                PillarType.WORK -> abs(score - target) * (if (score > target) weights.workOverloadImpact.multiplier else weights.workUnderloadImpact.multiplier)
                PillarType.HEALTH -> abs(score - target) * weights.healthNeglectImpact.multiplier
                else -> abs(score - target)
            }
            sumAbsDiff += diff
        }
        val distanceD = sumAbsDiff / effectiveActivePillars.size.toDouble()

        // 12. Harmony Score
        val rawScore = 100.0 - (distanceD * 1.2) - digitalNoiseR.toDouble()
        val bonus = if (input.digitalNoiseOption.isCleanMindBuff) 3 else 0
        val harmonyScore = (rawScore + bonus).roundToInt().coerceIn(0, 100)

        // Pillar details for all 8 pillars (with isActive flag)
        val pillarDetails = mapOf(
            PillarType.WORK to PillarScoreDetail(
                pillar = PillarType.WORK,
                score = finalWork,
                zone = PillarType.WORK.getZone(finalWork),
                note = if (finalWork in 40..70) "Foco saudável" else if (finalWork > 70) "Risco de estafa" else "Subutilizado",
                isActive = PillarType.WORK in effectiveActivePillars
            ),
            PillarType.HEALTH to PillarScoreDetail(
                pillar = PillarType.HEALTH,
                score = finalHealth,
                zone = PillarType.HEALTH.getZone(finalHealth),
                crossPenaltyApplied = healthPenalty,
                note = if (finalHealth in 40..70) "Vitalidade calibrada" else if (finalHealth < 40) "Corpo pedindo trégua" else "Pico de vigor",
                isActive = PillarType.HEALTH in effectiveActivePillars
            ),
            PillarType.CREATION to PillarScoreDetail(
                pillar = PillarType.CREATION,
                score = finalCreation,
                zone = PillarType.CREATION.getZone(finalCreation),
                note = if (finalCreation in 40..70) "Mente criativa ativa" else if (finalCreation < 40) "Pausa criativa prolongada" else "Imersão autoral profunda",
                isActive = PillarType.CREATION in effectiveActivePillars
            ),
            PillarType.FINANCE to PillarScoreDetail(
                pillar = PillarType.FINANCE,
                score = finalFinance,
                zone = PillarType.FINANCE.getZone(finalFinance),
                note = if (finalFinance in 40..70) "Orçamento equilibrado" else if (finalFinance < 40) "Gasto impulsivo detectado" else "Aportes consistentes",
                isActive = PillarType.FINANCE in effectiveActivePillars
            ),
            PillarType.READING to PillarScoreDetail(
                pillar = PillarType.READING,
                score = finalReading,
                zone = PillarType.READING.getZone(finalReading),
                crossPenaltyApplied = readingPenalty,
                note = if (finalReading in 40..70) "Estudo equilibrado" else if (finalReading < 40) "Alimentação mental nula" else "Saturação de leitura",
                isActive = PillarType.READING in effectiveActivePillars
            ),
            PillarType.LEISURE to PillarScoreDetail(
                pillar = PillarType.LEISURE,
                score = finalLeisure,
                zone = PillarType.LEISURE.getZone(finalLeisure),
                note = if (finalLeisure in 40..70) "Descompressão real" else if (finalLeisure < 40) "Sem respiro mental" else "Excesso de fuga",
                isActive = PillarType.LEISURE in effectiveActivePillars
            ),
            PillarType.RELATIONS to PillarScoreDetail(
                pillar = PillarType.RELATIONS,
                score = finalSocial,
                zone = PillarType.RELATIONS.getZone(finalSocial),
                crossPenaltyApplied = socialPenalty,
                note = if (finalSocial in 40..70) "Conexão genuína" else if (finalSocial < 40) "Isolamento detectado" else "Alta intensidade social",
                isActive = PillarType.RELATIONS in effectiveActivePillars
            ),
            PillarType.PURPOSE to PillarScoreDetail(
                pillar = PillarType.PURPOSE,
                score = finalPurpose,
                zone = PillarType.PURPOSE.getZone(finalPurpose),
                note = if (finalPurpose in 40..70) "Sentido e presença" else if (finalPurpose < 40) "Desconexão de sentido" else "Plenitude existencial",
                isActive = PillarType.PURPOSE in effectiveActivePillars
            )
        )

        // Status classification
        val (statusTitle, statusSubtitle) = when {
            harmonyScore >= 80 -> "Harmonia Pura" to "Métricas na Zona Dourada. Excelente equilíbrio dinâmico!"
            harmonyScore >= 65 -> "Equilibrado" to "Boa distribuição de energia. Ajustes finos trazem o ápice."
            harmonyScore >= 50 -> "Tensão Moderada" to "Alguns pilares estão drenando sua estabilidade geral."
            else -> "Risco de Burnout" to "Disparidade crítica entre pilares ou excesso severo."
        }

        // Micro-Quest Generation
        val microQuest = generateMicroQuest(
            work = finalWork,
            health = finalHealth,
            reading = finalReading,
            leisure = finalLeisure,
            social = finalSocial,
            noise = input.digitalNoiseOption,
            input = input
        )

        return HarmonyResult(
            workScore = finalWork,
            healthScore = finalHealth,
            creationScore = finalCreation,
            financeScore = finalFinance,
            purposeScore = finalPurpose,
            readingScore = finalReading,
            leisureScore = finalLeisure,
            socialScore = finalSocial,
            distancePenaltyD = distanceD,
            digitalNoisePenaltyR = digitalNoiseR,
            harmonyScore = harmonyScore,
            statusTitle = statusTitle,
            statusSubtitle = statusSubtitle,
            pillarDetails = pillarDetails,
            activeBuffs = activeBuffs,
            crossPenalties = crossPenalties,
            microQuest = microQuest,
            hasDigitalNoiseFog = hasNoiseFog,
            activePillars = effectiveActivePillars
        )
    }

    private fun generateMicroQuest(
        work: Int,
        health: Int,
        reading: Int,
        leisure: Int,
        social: Int,
        noise: DigitalNoiseOption,
        input: DailyCheckInInput
    ): MicroQuest {
        // Priority 1: High digital noise
        if (noise == DigitalNoiseOption.HOURS_2_PLUS || noise == DigitalNoiseOption.HOUR_1_TO_2) {
            return MicroQuest(
                id = "quest_detox_${System.currentTimeMillis()}",
                title = "Detox do Sono",
                description = "Coloque o smartphone em outro cômodo ou em modo avião 40 minutos antes de se deitar hoje.",
                targetPillar = PillarType.HEALTH,
                xpReward = 60
            )
        }

        // Priority 2: Work overload
        if (work > 75) {
            return MicroQuest(
                id = "quest_work_cutoff_${System.currentTimeMillis()}",
                title = "Toque de Recolher Digital",
                description = "Encerre o expediente pontualmente e faça uma caminhada de 15 minutos sem fones de ouvido.",
                targetPillar = PillarType.WORK,
                xpReward = 50
            )
        }

        // Priority 3: Find pillar furthest from 60
        val deviations = listOf(
            PillarType.WORK to abs(work - 60),
            PillarType.HEALTH to abs(health - 60),
            PillarType.CREATION to abs(input.creationOption.points - 60),
            PillarType.FINANCE to abs(input.financeOption.points - 60),
            PillarType.PURPOSE to abs(input.purposeOption.points - 60),
            PillarType.READING to abs(reading - 60),
            PillarType.LEISURE to abs(leisure - 60),
            PillarType.RELATIONS to abs(social - 60)
        ).sortedByDescending { it.second }

        val worstPillar = deviations.first().first

        return when (worstPillar) {
            PillarType.READING -> {
                if (reading < 40) {
                    MicroQuest(
                        id = "quest_read_start_${System.currentTimeMillis()}",
                        title = "Nutrição Intelectual",
                        description = "Leia 10 a 15 páginas de um livro físico com uma xícara de chá ou café.",
                        targetPillar = PillarType.READING,
                        xpReward = 45
                    )
                } else {
                    MicroQuest(
                        id = "quest_read_digest_${System.currentTimeMillis()}",
                        title = "Digestão de Ideias",
                        description = "Escreva em 3 tópicos os maiores aprendizados das suas leituras recentes.",
                        targetPillar = PillarType.READING,
                        xpReward = 40
                    )
                }
            }
            PillarType.HEALTH -> {
                if (input.movementOption == MovementOption.NONE) {
                    MicroQuest(
                        id = "quest_walk_${System.currentTimeMillis()}",
                        title = "Despertar Corporal",
                        description = "Faça 20 minutos de caminhada ao ar livre ou alongamento de coluna.",
                        targetPillar = PillarType.HEALTH,
                        xpReward = 50
                    )
                } else if (input.hydrationOption == HydrationOption.LOW) {
                    MicroQuest(
                        id = "quest_water_${System.currentTimeMillis()}",
                        title = "Garrafa da Harmonia",
                        description = "Mantenha uma garrafa de 1 litro ao lado da mesa e complete-a duas vezes ao longo do dia.",
                        targetPillar = PillarType.HEALTH,
                        xpReward = 40
                    )
                } else {
                    MicroQuest(
                        id = "quest_sleep_ritual_${System.currentTimeMillis()}",
                        title = "Ritual da Penumbra",
                        description = "Diminua as luzes de casa a partir das 21h e priorize dormir para garantir 7h a 8h de sono.",
                        targetPillar = PillarType.HEALTH,
                        xpReward = 50
                    )
                }
            }
            PillarType.RELATIONS -> {
                MicroQuest(
                    id = "quest_social_${System.currentTimeMillis()}",
                    title = "Vínculo Consciente",
                    description = "Envie uma mensagem carinhosa ou faça uma ligação de 10 minutos para um amigo ou familiar.",
                    targetPillar = PillarType.RELATIONS,
                    xpReward = 45
                )
            }
            PillarType.LEISURE -> {
                if (leisure < 40) {
                    MicroQuest(
                        id = "quest_leisure_rest_${System.currentTimeMillis()}",
                        title = "Descompressão Sagrada",
                        description = "Dedique 45 minutos a um hobby genuíno sem culpa (jogar, ouvir música, cozinhar algo bom).",
                        targetPillar = PillarType.LEISURE,
                        xpReward = 45
                    )
                } else {
                    MicroQuest(
                        id = "quest_leisure_curb_${System.currentTimeMillis()}",
                        title = "Lazer Intencional",
                        description = "Substitua 1 hora de telas passivas por uma atividade contemplativa ao ar livre.",
                        targetPillar = PillarType.LEISURE,
                        xpReward = 40
                    )
                }
            }
            PillarType.WORK -> {
                MicroQuest(
                    id = "quest_work_focus_${System.currentTimeMillis()}",
                    title = "Bloco de Ouro Matinal",
                    description = "Execute um bloco de 90 minutos de foco ininterrupto na sua tarefa mais importante da manhã.",
                    targetPillar = PillarType.WORK,
                    xpReward = 50
                )
            }
            PillarType.CREATION -> {
                MicroQuest(
                    id = "quest_maker_${System.currentTimeMillis()}",
                    title = "Sessão Maker & Inventiva",
                    description = "Reserve 45 minutos para construir robôs, codificar seu jogo, pintar ou trabalhar num projeto autoral.",
                    targetPillar = PillarType.CREATION,
                    xpReward = 50
                )
            }
            PillarType.FINANCE -> {
                MicroQuest(
                    id = "quest_finance_${System.currentTimeMillis()}",
                    title = "Orçamento Consciente",
                    description = "Revise seu extrato do dia e mantenha um dia completo livre de gastos supérfluos.",
                    targetPillar = PillarType.FINANCE,
                    xpReward = 45
                )
            }
            PillarType.PURPOSE -> {
                MicroQuest(
                    id = "quest_purpose_${System.currentTimeMillis()}",
                    title = "Diário de Significado",
                    description = "Escreva 3 coisas pelas quais você é grato e 1 ação prática de amanhã alinhada ao seu propósito maior.",
                    targetPillar = PillarType.PURPOSE,
                    xpReward = 50
                )
            }
        }
    }
}
