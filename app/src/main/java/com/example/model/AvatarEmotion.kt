package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DeficiencyAmber
import com.example.ui.theme.EmeraldHarmony
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OverloadRed

enum class AvatarEmotion(
    val title: String,
    val subtitle: String,
    val scoreRange: IntRange,
    val primaryColor: Color,
    val auraColor: Color,
    val defaultSpeech: String
) {
    HAPPY(
        title = "Radiante & Em Harmonia",
        subtitle = "Corpo e mente operando no ápice do equilíbrio!",
        scoreRange = 80..100,
        primaryColor = EmeraldHarmony,
        auraColor = EmeraldHarmony.copy(alpha = 0.25f),
        defaultSpeech = "Estou me sentindo incrível! Tudo fluindo no ritmo certo."
    ),
    TIRED(
        title = "Cansado & Pedindo Trégua",
        subtitle = "Sustentando a rotina, mas a bateria biológica está em baixa.",
        scoreRange = 60..79,
        primaryColor = GoldPrimary,
        auraColor = GoldPrimary.copy(alpha = 0.2f),
        defaultSpeech = "Ufa... O dia foi puxado. Uma pausa cairia muito bem agora."
    ),
    SAD(
        title = "Triste & Descompensado",
        subtitle = "Desgaste perceptível e desequilíbrio em áreas vitais.",
        scoreRange = 40..59,
        primaryColor = DeficiencyAmber,
        auraColor = DeficiencyAmber.copy(alpha = 0.2f),
        defaultSpeech = "Sinto que algo está fora do lugar. Preciso de mais descanso e ar puro..."
    ),
    EXHAUSTED(
        title = "Exausto & Esgotamento Crítico",
        subtitle = "Alerta de Burnout! Níveis de energia no limite absoluto.",
        scoreRange = 0..39,
        primaryColor = OverloadRed,
        auraColor = OverloadRed.copy(alpha = 0.25f),
        defaultSpeech = "Pane no sistema... Não aguento mais telas nem sobrecarga. Socorro!"
    );

    companion object {
        fun fromScore(score: Int): AvatarEmotion = when {
            score >= 80 -> HAPPY
            score >= 60 -> TIRED
            score >= 40 -> SAD
            else -> EXHAUSTED
        }

        fun getContextualSpeech(score: Int, crossPenalties: List<String>, activeBuffs: List<String>): String {
            val emotion = fromScore(score)
            return when {
                crossPenalties.any { it.contains("Sobrecarga", ignoreCase = true) } ->
                    "Trabalhar sem parar está fritando meus circuitos. Desconecta um pouco!"
                crossPenalties.any { it.contains("Ruído Digital", ignoreCase = true) } ->
                    "Minha cabeça está pesada com tanta tela e notificação... Névoa mental total."
                crossPenalties.any { it.contains("Saúde", ignoreCase = true) } ->
                    "Beba água e durma cedo hoje! Meu corpo está implorando por socorro."
                activeBuffs.any { it.contains("Mente Limpa", ignoreCase = true) } ->
                    "Mente limpa e leve! Zero poluição mental hoje. Continue assim!"
                activeBuffs.any { it.contains("Alinhamento Existencial", ignoreCase = true) } ->
                    "Sinto que cada passo de hoje teve sentido e propósito real!"
                crossPenalties.any { it.contains("Desconexão Existencial", ignoreCase = true) } ->
                    "Sentindo um certo vazio de propósito... O que traz significado para você?"
                activeBuffs.any { it.contains("Disciplina Circadiana", ignoreCase = true) } ->
                    "Acordei renovado! Dormir cedo fez toda a diferença."
                else -> emotion.defaultSpeech
            }
        }
    }
}
