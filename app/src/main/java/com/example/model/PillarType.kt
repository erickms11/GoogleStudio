package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class PillarType(
    val title: String,
    val description: String,
    val targetRange: IntRange = 40..70,
    val idealValue: Int = 60,
    val color: Color
) {
    WORK(
        title = "Trabalho & Projetos",
        description = "Foco ativo em realizações profissionais e projetos pessoais",
        color = PillarWork
    ),
    HEALTH(
        title = "Saúde & Vitalidade",
        description = "Composto de sono restaurador, hidratação, nutrição e movimento",
        color = PillarHealth
    ),
    CREATION(
        title = "Criação & Maker",
        description = "Robótica, gamedev, pintura, arte, invenções e projetos autorais",
        color = PillarCreation
    ),
    FINANCE(
        title = "Finanças & Controle",
        description = "Controle de gastos conscientes, aportes e tranquilidade orçamentária",
        color = PillarFinance
    ),
    READING(
        title = "Leitura & Estudos",
        description = "Nutrição intelectual intencional e expansão de repertório",
        color = PillarReading
    ),
    LEISURE(
        title = "Lazer Real",
        description = "Descompressão genuína e intencional (hobbies, passeios, jogos)",
        color = PillarLeisure
    ),
    RELATIONS(
        title = "Relações & Conexão",
        description = "Presença social significativa e vínculos afetivos",
        color = PillarRelations
    ),
    PURPOSE(
        title = "Propósito & Existencialismo",
        description = "Clareza de sentido de vida, ações com significado e alinhamento de valores",
        color = PillarPurpose
    );

    fun getZone(score: Int): PillarZone {
        return when {
            score < targetRange.first -> PillarZone.DEFICIENCY
            score in targetRange -> PillarZone.GOLDEN_ZONE
            else -> PillarZone.OVERLOAD
        }
    }
}

enum class PillarZone(val label: String, val color: Color) {
    DEFICIENCY("Deficiência", DeficiencyAmber),
    GOLDEN_ZONE("Zona Dourada", EmeraldHarmony),
    OVERLOAD("Sobrecarga", OverloadRed)
}

// Option enums for check-in
enum class WorkOption(val label: String, val hours: Int) {
    H0("0h", 0),
    H4("4h", 4),
    H6("6h", 6),
    H8("8h", 8),
    H10("10h", 10),
    H12_PLUS("12h+", 12)
}

enum class SleepOption(val label: String, val points: Int, val detail: String) {
    LESS_THAN_6("<6h", 10, "Privação"),
    HOURS_7_8("7-8h", 25, "Ideal"),
    HOURS_9_PLUS("9h+", 20, "Prolongado")
}

enum class HydrationOption(val label: String, val points: Int, val detail: String) {
    LOW("Pouca", 5, "<1L"),
    REGULAR("Regular", 12, "1.5L - 2L"),
    OPTIMAL("Ótima", 18, "2.5L+")
}

enum class NutritionOption(val label: String, val points: Int, val detail: String) {
    HEAVY("Ultraprocessada", 5, "Pesada"),
    BALANCED("Equilibrada", 13, "Mista"),
    CLEAN("Nutritiva / Limpa", 18, "Integral")
}

enum class MovementOption(val label: String, val points: Int, val detail: String) {
    NONE("0m", 0, "Sedentário"),
    MIN_30_45("30-45m", 12, "Ativo"),
    HOUR_1_PLUS("1h+", 18, "Intenso")
}

enum class ReadingOption(val label: String, val points: Int) {
    NONE("0 min", 15),
    MIN_15("10-15 min", 45),
    MIN_30("30 min", 65),
    HOUR_1("1 hora", 65),
    HOURS_2_PLUS(">2 horas", 85)
}

enum class LeisureOption(val label: String, val points: Int) {
    H0("0h", 20),
    H1("1h", 50),
    H2_3("2-3h", 65),
    H5_PLUS("5h+", 90)
}

enum class SocialOption(val label: String, val points: Int, val detail: String) {
    DISTANT("Distante / Isolado", 20, "Pouco contato"),
    ROUTINE("Rotina Compartilhada", 55, "Companhia diária"),
    MEMORABLE("Encontro Marcante", 85, "Conexão profunda")
}

enum class DigitalNoiseOption(
    val label: String,
    val penaltyR: Int,
    val detail: String,
    val isCleanMindBuff: Boolean = false
) {
    LESS_20_MIN("<20 min", 0, "Buff Mente Limpa (+15% XP)", isCleanMindBuff = true),
    MIN_30_TO_1H("30m-1h", 5, "Impacto Neutro"),
    HOUR_1_TO_2("1h-2h", 15, "-10 Leitura, -5 Sono"),
    HOURS_2_PLUS("2h+", 30, "-20 Leitura, -15 Saúde, névoa")
}

enum class BedtimeOption(val label: String, val detail: String, val points: Int) {
    BEFORE_23("< 23h", "Dormir Cedo", 15),
    BETWEEN_23_00("23h - 00h", "Regular", 10),
    AFTER_00("> 00h", "Madrugada", 0)
}

enum class WakeTimeOption(val label: String, val detail: String, val points: Int) {
    EARLY_DAWN("5h - 6h30", "Acordar Cedo", 15),
    REGULAR("6h30 - 8h", "Regular", 10),
    LATE("> 8h30", "Tardio", 0)
}

enum class IntimacyOption(val label: String, val detail: String, val bonus: Int) {
    FULFILLING("Plena & Conectada", "Vitalidade e conexão íntima", 5),
    BALANCED("Equilibrada", "Presente e harmoniosa", 2),
    PAUSE("Em Pausa", "Recarga / Foco individual", 0),
    LOW_ENERGY("Baixa Energia", "Fadiga ou estresse", -3)
}

enum class FeelingOption(val label: String, val detail: String, val emoji: String) {
    SERENE("Sereno & Em Paz", "Equilíbrio emocional", "🌿"),
    FOCUSED("Focado & Motivado", "Clareza produtiva", "⚡"),
    OVERWHELMED("Ansioso / Tenso", "Alerta anti-burnout", "⚠️"),
    EXHAUSTED("Esgotado / Baixo", "Necessidade de repouso", "🛑")
}

enum class RoutineExamsOption(val label: String, val detail: String, val isProtected: Boolean) {
    UP_TO_DATE("Em Dia", "Check-up nos últimos 12 meses", true),
    SCHEDULED("Agendado", "Consulta ou exames marcados", true),
    PENDING("Pendente", "Mais de 1 ano sem revisão", false)
}

enum class CreationOption(val label: String, val points: Int, val detail: String) {
    NONE("0 min", 30, "Pausa / Recarga"),
    MIN_30("30 min", 50, "Micro-avanço"),
    HOURS_1_2("1h - 2h", 65, "Estado de Flow / Ideal"),
    HOURS_3_PLUS("3h+", 85, "Imersão Criativa")
}

enum class FinanceOption(val label: String, val points: Int, val detail: String) {
    CONTROLLED("Aporte & Meta", 65, "Dentro do orçamento & investindo"),
    BALANCED("Equilibrado", 60, "Sem gastos supérfluos hoje"),
    CONSCIOUS("Gasto Planejado", 55, "Compra consciente de projeto/lazer"),
    IMPULSIVE("Gasto Impulsivo", 30, "Desvio orçamentário / Alerta")
}

enum class PurposeOption(val label: String, val points: Int, val detail: String) {
    ALIGNED_ACTIVE("Pleno & Ações Práticas", 65, "Passos concretos alinhados ao sentido"),
    CLEAR_MINDFUL("Consciente & Presente", 60, "Clareza de valores e presença no dia"),
    NEUTRAL_BUSY("Neutro / Automático", 45, "Dia corrido sem reflexão de propósito"),
    LOST_QUESTIONING("Desalinhado / Vazio", 25, "Sensação de desconexão ou crise de sentido")
}



