package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.DeficiencyAmber
import com.example.ui.theme.EmeraldHarmony
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OverloadRed

enum class ImpactLevel(
    val label: String,
    val multiplier: Double,
    val description: String,
    val color: Color
) {
    LIGHT(
        label = "Leve",
        multiplier = 0.5,
        description = "Tolerância alta. Penalidades e custos reduzidos pela metade (-50%).",
        color = Color(0xFF64B5F6)
    ),
    MODERATE(
        label = "Moderado",
        multiplier = 1.0,
        description = "Padrão de equilíbrio. Penalidades proporcionais clássicas (1.0x).",
        color = EmeraldHarmony
    ),
    STRICT(
        label = "Rigoroso",
        multiplier = 1.5,
        description = "Exigência elevada. Custos e impactos aumentados em +50% (1.5x).",
        color = GoldPrimary
    ),
    EXTREME(
        label = "Extremo",
        multiplier = 2.0,
        description = "Tolerância zero. Dobra o peso e penalidade de desvios e sobrecargas (2.0x).",
        color = OverloadRed
    );

    companion object {
        fun fromIndex(index: Int): ImpactLevel = entries.getOrElse(index.coerceIn(0, 3)) { MODERATE }
    }

    val sliderPosition: Float
        get() = ordinal.toFloat()
}

data class PillarWeightsConfig(
    val workUnderloadImpact: ImpactLevel = ImpactLevel.MODERATE,
    val workOverloadImpact: ImpactLevel = ImpactLevel.MODERATE,
    val healthNeglectImpact: ImpactLevel = ImpactLevel.MODERATE,
    val digitalNoiseImpact: ImpactLevel = ImpactLevel.MODERATE,
    val leisureOverloadImpact: ImpactLevel = ImpactLevel.MODERATE
)
