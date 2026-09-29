package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CheckInEntity
import com.example.data.EquilibriumRepository
import com.example.logic.HarmonyCalculator
import com.example.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EquilibriumViewModel(
    private val repository: EquilibriumRepository
) : ViewModel() {

    // Draft input for live calculation during Check-in
    private val _draftInput = MutableStateFlow(DailyCheckInInput())
    val draftInput: StateFlow<DailyCheckInInput> = _draftInput.asStateFlow()

    val pillarWeightsConfig: StateFlow<PillarWeightsConfig> = repository.pillarWeightsConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PillarWeightsConfig()
        )

    // Active Pillars Selection (default all 8 pillars enabled)
    private val _activePillars = MutableStateFlow<Set<PillarType>>(PillarType.entries.toSet())
    val activePillars: StateFlow<Set<PillarType>> = _activePillars.asStateFlow()

    fun togglePillar(pillar: PillarType) {
        val current = _activePillars.value
        if (current.contains(pillar)) {
            // Safety rule: at least 2 pillars must remain active
            if (current.size > 2) {
                _activePillars.value = current - pillar
            }
        } else {
            _activePillars.value = current + pillar
        }
    }

    fun isPillarActive(pillar: PillarType): Boolean = _activePillars.value.contains(pillar)

    fun resetActivePillars() {
        _activePillars.value = PillarType.entries.toSet()
    }

    // Real-time calculated harmony result based on draft input, active weights, and active pillars
    val liveResult: StateFlow<HarmonyResult> = combine(_draftInput, pillarWeightsConfig, _activePillars) { input, weights, activePillars ->
        HarmonyCalculator.calculate(input, weights, activePillars)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HarmonyCalculator.calculate(DailyCheckInInput())
    )

    val latestCheckIn: StateFlow<CheckInEntity?> = repository.latestCheckIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val allCheckIns: StateFlow<List<CheckInEntity>> = repository.allCheckIns
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val monthlyGoals: StateFlow<MonthlyGoals> = repository.monthlyGoals
        .map { entity ->
            if (entity != null) {
                MonthlyGoals(
                    targetSleepHours = entity.targetSleepHours,
                    targetReadingMinutesDaily = entity.targetReadingMinutesDaily,
                    targetLeisureHoursDaily = entity.targetLeisureHoursDaily,
                    targetWorkHoursDaily = entity.targetWorkHoursDaily,
                    targetWaterLitersDaily = entity.targetWaterLitersDaily,
                    targetRoutineCheckupDone = entity.targetRoutineCheckupDone
                )
            } else MonthlyGoals()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MonthlyGoals()
        )

    val goalsProgress: StateFlow<List<GoalMetricProgress>> = combine(allCheckIns, monthlyGoals) { checkIns, goals ->
        calculateGoalProgress(checkIns, goals)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val disciplineMetric: StateFlow<DisciplineMetric> = combine(
        latestCheckIn,
        draftInput,
        allCheckIns
    ) { latest, draft, history ->
        calculateDisciplineMetric(latest, draft, history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DisciplineMetric(
            score = 100,
            sleptEarly = true,
            wokeEarly = true,
            bedtimeLabel = "< 23h",
            wakeTimeLabel = "5h - 6h30",
            streakDays = 1,
            weeklyAdherence = 100,
            statusLabel = "Ritmo Solar Impecável"
        )
    )

    private val _selectedPillar = MutableStateFlow<PillarType?>(null)
    val selectedPillar: StateFlow<PillarType?> = _selectedPillar.asStateFlow()

    private val _showVerdictDialog = MutableStateFlow(false)
    val showVerdictDialog: StateFlow<Boolean> = _showVerdictDialog.asStateFlow()

    private val _lastSavedResult = MutableStateFlow<HarmonyResult?>(null)
    val lastSavedResult: StateFlow<HarmonyResult?> = _lastSavedResult.asStateFlow()

    private val _showPhilosophyGuide = MutableStateFlow(false)
    val showPhilosophyGuide: StateFlow<Boolean> = _showPhilosophyGuide.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectPillar(pillar: PillarType?) {
        _selectedPillar.value = pillar
    }

    fun setShowVerdictDialog(show: Boolean) {
        _showVerdictDialog.value = show
    }

    fun setShowPhilosophyGuide(show: Boolean) {
        _showPhilosophyGuide.value = show
    }

    fun setShowSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    // Input state updater methods
    fun setDayOff(isDayOff: Boolean) {
        _draftInput.update { it.copy(isDayOff = isDayOff) }
    }

    fun setWorkOption(option: WorkOption) {
        _draftInput.update { it.copy(workOption = option) }
    }

    fun setSleepOption(option: SleepOption) {
        _draftInput.update { it.copy(sleepOption = option) }
    }

    fun setBedtimeOption(option: BedtimeOption) {
        _draftInput.update { it.copy(bedtimeOption = option) }
    }

    fun setWakeTimeOption(option: WakeTimeOption) {
        _draftInput.update { it.copy(wakeTimeOption = option) }
    }

    fun setHydrationOption(option: HydrationOption) {
        _draftInput.update { it.copy(hydrationOption = option) }
    }

    fun setNutritionOption(option: NutritionOption) {
        _draftInput.update { it.copy(nutritionOption = option) }
    }

    fun setMovementOption(option: MovementOption) {
        _draftInput.update { it.copy(movementOption = option) }
    }

    fun setIntimacyOption(option: IntimacyOption) {
        _draftInput.update { it.copy(intimacyOption = option) }
    }

    fun setFeelingOption(option: FeelingOption) {
        _draftInput.update { it.copy(feelingOption = option) }
    }

    fun setRoutineExamsOption(option: RoutineExamsOption) {
        _draftInput.update { it.copy(routineExamsOption = option) }
    }

    fun setMedicalCheckup(valid: Boolean) {
        _draftInput.update { it.copy(medicalCheckupValid = valid) }
    }

    fun setReadingOption(option: ReadingOption) {
        _draftInput.update { it.copy(readingOption = option) }
    }

    fun setLeisureOption(option: LeisureOption) {
        _draftInput.update { it.copy(leisureOption = option) }
    }

    fun setSocialOption(option: SocialOption) {
        _draftInput.update { it.copy(socialOption = option) }
    }

    fun setCreationOption(option: CreationOption) {
        _draftInput.update { it.copy(creationOption = option) }
    }

    fun setFinanceOption(option: FinanceOption) {
        _draftInput.update { it.copy(financeOption = option) }
    }

    fun setPurposeOption(option: PurposeOption) {
        _draftInput.update { it.copy(purposeOption = option) }
    }

    fun setDigitalNoiseOption(option: DigitalNoiseOption) {
        _draftInput.update { it.copy(digitalNoiseOption = option) }
    }

    fun resetAppToZero(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.resetAllData()
            _draftInput.value = DailyCheckInInput()
            _lastSavedResult.value = null
            _selectedPillar.value = null
            onDone()
        }
    }

    fun exportDataToJson(context: Context) {
        viewModelScope.launch {
            val checkIns = allCheckIns.value
            val goals = monthlyGoals.value
            com.example.util.DataExporter.exportAndShare(context, checkIns, goals)
        }
    }

    fun restoreDemoData(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.restoreDemoData()
            _draftInput.value = DailyCheckInInput()
            onDone()
        }
    }

    fun calibrateInitialBaseline(baseline: DailyCheckInInput) {
        _draftInput.value = baseline
    }

    fun submitCheckIn(onCompleted: () -> Unit = {}) {
        viewModelScope.launch {
            val input = _draftInput.value
            val weights = pillarWeightsConfig.value
            val activePillars = _activePillars.value
            val result = HarmonyCalculator.calculate(input, weights, activePillars)
            repository.saveCheckIn(input, result)
            _lastSavedResult.value = result
            _showVerdictDialog.value = true
            onCompleted()
        }
    }

    fun updateWeightsConfig(config: PillarWeightsConfig) {
        viewModelScope.launch {
            repository.saveWeightsConfig(config)
        }
    }

    fun resetWeightsConfigToDefault() {
        viewModelScope.launch {
            repository.saveWeightsConfig(PillarWeightsConfig())
        }
    }

    fun toggleQuestCompleted(checkInId: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.setQuestCompleted(checkInId, completed)
        }
    }

    fun saveMonthlyGoals(goals: MonthlyGoals) {
        viewModelScope.launch {
            repository.saveGoals(
                com.example.data.MonthlyGoalsEntity(
                    targetSleepHours = goals.targetSleepHours,
                    targetReadingMinutesDaily = goals.targetReadingMinutesDaily,
                    targetLeisureHoursDaily = goals.targetLeisureHoursDaily,
                    targetWorkHoursDaily = goals.targetWorkHoursDaily,
                    targetWaterLitersDaily = goals.targetWaterLitersDaily,
                    targetRoutineCheckupDone = goals.targetRoutineCheckupDone
                )
            )
        }
    }

    private fun calculateGoalProgress(
        checkIns: List<CheckInEntity>,
        goals: MonthlyGoals
    ): List<GoalMetricProgress> {
        val totalDays = if (checkIns.isNotEmpty()) checkIns.size else 1

        // 1. Sono Médio
        val avgSleep = if (checkIns.isNotEmpty()) {
            checkIns.map { entity ->
                when {
                    entity.sleepOptionLabel.contains("<6") -> 5.5f
                    entity.sleepOptionLabel.contains("7-8") -> 7.5f
                    else -> 9.0f
                }
            }.average().toFloat()
        } else goals.targetSleepHours

        val sleepPercent = ((avgSleep / goals.targetSleepHours) * 100).toInt().coerceIn(0, 150)

        // 2. Leitura Média
        val avgReading = if (checkIns.isNotEmpty()) {
            checkIns.map { entity ->
                when {
                    entity.readingOptionLabel.contains("10-15") -> 15
                    entity.readingOptionLabel.contains("30") -> 30
                    entity.readingOptionLabel.contains("1 hora") -> 60
                    entity.readingOptionLabel.contains(">2") -> 90
                    else -> 0
                }
            }.average().toInt()
        } else goals.targetReadingMinutesDaily

        val readingPercent = if (goals.targetReadingMinutesDaily > 0)
            ((avgReading.toFloat() / goals.targetReadingMinutesDaily) * 100).toInt().coerceIn(0, 150)
        else 100

        // 3. Lazer Diário
        val avgLeisure = if (checkIns.isNotEmpty()) {
            checkIns.map { entity ->
                when {
                    entity.leisureOptionLabel.contains("1h") -> 1.0f
                    entity.leisureOptionLabel.contains("2-3") -> 2.5f
                    entity.leisureOptionLabel.contains("5h+") -> 5.0f
                    else -> 0f
                }
            }.average().toFloat()
        } else goals.targetLeisureHoursDaily

        val leisurePercent = if (goals.targetLeisureHoursDaily > 0)
            ((avgLeisure / goals.targetLeisureHoursDaily) * 100).toInt().coerceIn(0, 150)
        else 100

        // 4. Trabalho Controlado
        val avgWork = if (checkIns.isNotEmpty()) {
            checkIns.map { entity ->
                when {
                    entity.workOptionLabel.contains("4h") -> 4f
                    entity.workOptionLabel.contains("6h") -> 6f
                    entity.workOptionLabel.contains("8h") -> 8f
                    entity.workOptionLabel.contains("10h") -> 10f
                    entity.workOptionLabel.contains("12h") -> 12f
                    else -> 0f
                }
            }.average().toFloat()
        } else goals.targetWorkHoursDaily

        val workPercent = if (avgWork <= goals.targetWorkHoursDaily) 100 else
            (100 - ((avgWork - goals.targetWorkHoursDaily) * 15)).toInt().coerceIn(20, 100)

        // 5. Exames de Rotina
        val checkupValid = checkIns.firstOrNull()?.routineExamsLabel?.contains("Em Dia") ?: true

        return listOf(
            GoalMetricProgress(
                title = "Média de Sono Noturno",
                currentFormatted = String.format(java.util.Locale.US, "%.1fh / noite", avgSleep),
                targetFormatted = String.format(java.util.Locale.US, "Meta: %.1fh", goals.targetSleepHours),
                progressPercent = sleepPercent,
                isAchieved = avgSleep >= (goals.targetSleepHours - 0.5f),
                pillar = PillarType.HEALTH,
                motivationalTip = if (avgSleep >= goals.targetSleepHours) "Excelente descanso celular!" else "Priorize deitar 30m mais cedo."
            ),
            GoalMetricProgress(
                title = "Hábito Diário de Leitura",
                currentFormatted = "$avgReading min / dia",
                targetFormatted = "Meta: ${goals.targetReadingMinutesDaily} min",
                progressPercent = readingPercent,
                isAchieved = avgReading >= goals.targetReadingMinutesDaily,
                pillar = PillarType.READING,
                motivationalTip = if (avgReading >= goals.targetReadingMinutesDaily) "Repertório intelectual expandido!" else "10 páginas por dia fazem a diferença."
            ),
            GoalMetricProgress(
                title = "Lazer Consciente & Pausas",
                currentFormatted = String.format(java.util.Locale.US, "%.1fh / dia", avgLeisure),
                targetFormatted = String.format(java.util.Locale.US, "Meta: %.1fh", goals.targetLeisureHoursDaily),
                progressPercent = leisurePercent,
                isAchieved = avgLeisure >= (goals.targetLeisureHoursDaily * 0.7f),
                pillar = PillarType.LEISURE,
                motivationalTip = "Descompressão real sem culpa recarrega a criatividade."
            ),
            GoalMetricProgress(
                title = "Foco Profissional Saudável",
                currentFormatted = String.format(java.util.Locale.US, "%.1fh / dia", avgWork),
                targetFormatted = String.format(java.util.Locale.US, "Teto: %.1fh", goals.targetWorkHoursDaily),
                progressPercent = workPercent,
                isAchieved = avgWork <= goals.targetWorkHoursDaily,
                pillar = PillarType.WORK,
                motivationalTip = if (avgWork <= goals.targetWorkHoursDaily) "Trabalho sem sobrecarga!" else "Cuidado: horas extras canibalizam saúde."
            ),
            GoalMetricProgress(
                title = "Exames & Manutenção Preventiva",
                currentFormatted = if (checkupValid) "Exames em dia" else "Pendente",
                targetFormatted = "Meta: Anual em dia",
                progressPercent = if (checkupValid) 100 else 40,
                isAchieved = checkupValid,
                pillar = PillarType.HEALTH,
                motivationalTip = if (checkupValid) "Escudo de longevidade ativo!" else "Agende seus exames de rotina este mês."
            ),
            GoalMetricProgress(
                title = "Criação & Projetos Maker",
                currentFormatted = if (checkIns.any { it.creationOptionLabel.contains("1h") || it.creationOptionLabel.contains("3h") }) "Prática Ativa" else "Pausa Criativa",
                targetFormatted = "Meta: 3+ sessões / mês",
                progressPercent = if (checkIns.count { it.creationOptionLabel.contains("1h") || it.creationOptionLabel.contains("3h") } >= 1) 100 else 60,
                isAchieved = true,
                pillar = PillarType.CREATION,
                motivationalTip = "Construir robôs, criar jogos ou pintar nutre a inventividade humana."
            ),
            GoalMetricProgress(
                title = "Orçamento & Gastos Conscientes",
                currentFormatted = if (checkIns.none { it.financeOptionLabel.contains("Impulsivo") }) "100% Controlado" else "Atenção a Impulsos",
                targetFormatted = "Meta: Zero impulsos",
                progressPercent = if (checkIns.none { it.financeOptionLabel.contains("Impulsivo") }) 100 else 50,
                isAchieved = checkIns.none { it.financeOptionLabel.contains("Impulsivo") },
                pillar = PillarType.FINANCE,
                motivationalTip = "A tranquilidade financeira liberta a mente para focar em grandes criações."
            )
        )
    }

    fun setHabitSleepingEarly(sleptEarly: Boolean) {
        val newBedtime = if (sleptEarly) BedtimeOption.BEFORE_23 else BedtimeOption.AFTER_00
        _draftInput.update { it.copy(bedtimeOption = newBedtime) }
    }

    fun setHabitWakingEarly(wokeEarly: Boolean) {
        val newWakeTime = if (wokeEarly) WakeTimeOption.EARLY_DAWN else WakeTimeOption.REGULAR
        _draftInput.update { it.copy(wakeTimeOption = newWakeTime) }
    }

    private fun isBedtimeEarly(bedtimeLabel: String): Boolean {
        return bedtimeLabel.contains("< 23h") || bedtimeLabel.contains("Antes")
    }

    private fun isWakeTimeEarly(wakeTimeLabel: String): Boolean {
        return wakeTimeLabel.contains("5h - 6h30") || wakeTimeLabel.contains("Antes das 5h")
    }

    private fun calculateDisciplineMetric(
        latest: CheckInEntity?,
        draft: DailyCheckInInput,
        history: List<CheckInEntity>
    ): DisciplineMetric {
        val bedtime = latest?.bedtimeLabel ?: draft.bedtimeOption.label
        val wakeTime = latest?.wakeTimeLabel ?: draft.wakeTimeOption.label

        val sleptEarly = isBedtimeEarly(bedtime)
        val wokeEarly = isWakeTimeEarly(wakeTime)

        val score = when {
            sleptEarly && wokeEarly -> 100
            sleptEarly || wokeEarly -> 50
            else -> 0
        }

        // Calculate consecutive streak (days with discipline score >= 50%)
        var streak = 0
        if (score >= 50) {
            streak = 1
            for (checkIn in history) {
                if (latest != null && checkIn.id == latest.id) continue
                val cSlept = isBedtimeEarly(checkIn.bedtimeLabel)
                val cWoke = isWakeTimeEarly(checkIn.wakeTimeLabel)
                if (cSlept || cWoke) {
                    streak++
                } else {
                    break
                }
            }
        } else {
            for (checkIn in history) {
                val cSlept = isBedtimeEarly(checkIn.bedtimeLabel)
                val cWoke = isWakeTimeEarly(checkIn.wakeTimeLabel)
                if (cSlept || cWoke) {
                    streak++
                } else {
                    break
                }
            }
        }

        val last7 = history.take(7)
        val adherence = if (last7.isNotEmpty()) {
            val totalScore = last7.sumOf { c ->
                val s = if (isBedtimeEarly(c.bedtimeLabel)) 50 else 0
                val w = if (isWakeTimeEarly(c.wakeTimeLabel)) 50 else 0
                s + w
            }
            totalScore / last7.size
        } else {
            score
        }

        val status = when {
            score == 100 -> "Ritmo Solar Impecável"
            score == 50 -> "Alinhamento Parcial"
            else -> "Ajuste Circadiano Necessário"
        }

        return DisciplineMetric(
            score = score,
            sleptEarly = sleptEarly,
            wokeEarly = wokeEarly,
            bedtimeLabel = bedtime,
            wakeTimeLabel = wakeTime,
            streakDays = maxOf(streak, if (score >= 50) 1 else 0),
            weeklyAdherence = adherence,
            statusLabel = status
        )
    }

    class Factory(private val repository: EquilibriumRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EquilibriumViewModel::class.java)) {
                return EquilibriumViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
