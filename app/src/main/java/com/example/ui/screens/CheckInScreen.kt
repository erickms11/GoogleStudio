package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val draftInput by viewModel.draftInput.collectAsState()
    val liveResult by viewModel.liveResult.collectAsState()
    val activePillars by viewModel.activePillars.collectAsState()

    val scoreColor = when {
        liveResult.harmonyScore >= 80 -> EmeraldHarmony
        liveResult.harmonyScore >= 65 -> GoldPrimary
        liveResult.harmonyScore >= 50 -> DeficiencyAmber
        else -> OverloadRed
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Check-in Noturno",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Calibração de harmonia diária",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("checkin_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    // Live mini score pill in header
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = scoreColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(scoreColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${liveResult.harmonyScore}%",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = scoreColor
                                )
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(
                            text = "Índice Projetado",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "${liveResult.harmonyScore}% • ${liveResult.statusTitle}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = scoreColor
                            )
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.submitCheckIn {
                                onNavigateBack()
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scoreColor,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("submit_checkin_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Equilibrar o Dia",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // 1. BLOCO TRABALHO
            if (activePillars.contains(PillarType.WORK)) {
                CheckInBlockCard(
                    title = "Trabalho & Projetos",
                    pillar = PillarType.WORK,
                    subtitle = "Foco ativo em realizações profissionais ou acadêmicas."
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Dia de Folga / Descanso",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                        )
                        Switch(
                            checked = draftInput.isDayOff,
                            onCheckedChange = { viewModel.setDayOff(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = EmeraldHarmony,
                                checkedTrackColor = EmeraldDark
                            ),
                            modifier = Modifier.testTag("work_day_off_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Horas dedicadas hoje:",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        WorkOption.values().forEach { option ->
                            PillButton(
                                text = option.label,
                                isSelected = draftInput.workOption == option,
                                activeColor = PillarWork,
                                onClick = { viewModel.setWorkOption(option) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HelperText(
                        text = if (draftInput.isDayOff)
                            "Folga ativa: 0h é equilibrado (60 pts). Trabalhar em folgas gera alerta."
                        else
                            "Equilíbrio: 6h a 8h. >9h penaliza Saúde (-10 pts) e Relações (-10 pts)."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 2. BLOCO SAÚDE & VITALIDADE
            if (activePillars.contains(PillarType.HEALTH)) {
                CheckInBlockCard(
                    title = "Saúde & Vitalidade",
                    pillar = PillarType.HEALTH,
                    subtitle = "4 micro-indicadores táteis (sono, água, comida e movimento)."
                ) {
                // Sono
                SubSelectorRow(
                    label = "Sono Noturno:",
                    options = listOf(
                        SleepOption.LESS_THAN_6 to "<6h (10p)",
                        SleepOption.HOURS_7_8 to "7-8h (35p)",
                        SleepOption.HOURS_9_PLUS to "9h+ (25p)"
                    ),
                    selected = draftInput.sleepOption,
                    onSelect = { viewModel.setSleepOption(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hidratação
                SubSelectorRow(
                    label = "Hidratação:",
                    options = listOf(
                        HydrationOption.LOW to "Pouca (5p)",
                        HydrationOption.REGULAR to "Ok (15p)",
                        HydrationOption.OPTIMAL to "Ótima (25p)"
                    ),
                    selected = draftInput.hydrationOption,
                    onSelect = { viewModel.setHydrationOption(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Alimentação
                SubSelectorRow(
                    label = "Alimentação:",
                    options = listOf(
                        NutritionOption.HEAVY to "Pesada (5p)",
                        NutritionOption.BALANCED to "Mista (15p)",
                        NutritionOption.CLEAN to "Limpa (20p)"
                    ),
                    selected = draftInput.nutritionOption,
                    onSelect = { viewModel.setNutritionOption(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Movimento / Treino
                SubSelectorRow(
                    label = "Movimento / Treino:",
                    options = listOf(
                        MovementOption.NONE to "0m (0p)",
                        MovementOption.MIN_30_45 to "30-45m (15p)",
                        MovementOption.HOUR_1_PLUS to "1h+ (20p)"
                    ),
                    selected = draftInput.movementOption,
                    onSelect = { viewModel.setMovementOption(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rare maintenance buff switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Exames Médicos em Dia (12 meses)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Escudo de Check-up: impede queda brusca de saúde",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Switch(
                        checked = draftInput.medicalCheckupValid,
                        onCheckedChange = { viewModel.setMedicalCheckup(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = EmeraldHarmony,
                            checkedTrackColor = EmeraldDark
                        ),
                        modifier = Modifier.testTag("medical_checkup_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BLOCO DISCIPLINA & RITMO CIRCADIANO (Dormir Cedo & Acordar Cedo)
            CheckInBlockCard(
                title = "Disciplina & Ritmo Circadiano",
                pillar = PillarType.HEALTH,
                subtitle = "Sincronização biológica: dormir cedo e despertar com a alvorada."
            ) {
                // Horário de Dormir
                SubSelectorRow(
                    label = "Horário de Dormir:",
                    options = listOf(
                        BedtimeOption.BEFORE_23 to "< 23h (Cedo)",
                        BedtimeOption.BETWEEN_23_00 to "23h - 00h",
                        BedtimeOption.AFTER_00 to "> 00h (Madrugada)"
                    ),
                    selected = draftInput.bedtimeOption,
                    onSelect = { viewModel.setBedtimeOption(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Horário de Acordar
                SubSelectorRow(
                    label = "Horário de Acordar:",
                    options = listOf(
                        WakeTimeOption.EARLY_DAWN to "5h - 6h30 (Alvorada)",
                        WakeTimeOption.REGULAR to "6h30 - 8h",
                        WakeTimeOption.LATE to "> 8h30 (Tardio)"
                    ),
                    selected = draftInput.wakeTimeOption,
                    onSelect = { viewModel.setWakeTimeOption(it) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                val isCircadianActive = draftInput.bedtimeOption == BedtimeOption.BEFORE_23 && draftInput.wakeTimeOption == WakeTimeOption.EARLY_DAWN
                if (isCircadianActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldDark.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Buff Disciplina Circadiana Ativo (+10 pts Vigor & Foco)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                } else if (draftInput.bedtimeOption == BedtimeOption.AFTER_00) {
                    HelperText("Atenção: Dormir de madrugada causa dessincronização circadiana (-5 pts Saúde).")
                } else {
                    HelperText("Dormir antes das 23h e despertar entre 5h e 6h30 desbloqueia o Buff de Disciplina Circadiana.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NOVO: BLOCO SAÚDE ÍNTIMA & SEXUALIDADE
            CheckInBlockCard(
                title = "Saúde Íntima & Vida Sexual",
                pillar = PillarType.HEALTH,
                subtitle = "Vitalidade hormonal, expressão íntima e conexão afetiva."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IntimacyOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.intimacyOption == option,
                            activeColor = PillarRelations,
                            onClick = { viewModel.setIntimacyOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("A saúde íntima equilibrada é reflexo de vitalidade física e conexão emocional.")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NOVO: BLOCO SENTIMENTOS & ESTADO EMOCIONAL
            CheckInBlockCard(
                title = "Sentimentos & Estado de Espírito",
                pillar = PillarType.HEALTH,
                subtitle = "Percepção do seu humor e bem-estar psicológico hoje."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FeelingOption.values().forEach { option ->
                        val isSelected = draftInput.feelingOption == option
                        val activeColor = when (option) {
                            FeelingOption.SERENE -> EmeraldHarmony
                            FeelingOption.FOCUSED -> GoldPrimary
                            FeelingOption.OVERWHELMED -> DeficiencyAmber
                            FeelingOption.EXHAUSTED -> OverloadRed
                        }
                        PillButton(
                            text = "${option.emoji} ${option.label.split(" ")[0]}",
                            isSelected = isSelected,
                            activeColor = activeColor,
                            onClick = { viewModel.setFeelingOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Reconhecer a sobrecarga emocional a tempo é o pilar central da prevenção anti-burnout.")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NOVO: BLOCO EXAMES DE ROTINA & PREVENÇÃO
            CheckInBlockCard(
                title = "Exames de Rotina & Manutenção",
                pillar = PillarType.HEALTH,
                subtitle = "Acompanhamento médico anual, exames laboratoriais e preventivos."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoutineExamsOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.routineExamsOption == option,
                            activeColor = if (option.isProtected) EmeraldHarmony else DeficiencyAmber,
                            onClick = { viewModel.setRoutineExamsOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Check-up atualizado nos últimos 12 meses ativa o escudo de proteção da vitalidade.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. BLOCO LEITURA & ESTUDOS
        if (activePillars.contains(PillarType.READING)) {
            CheckInBlockCard(
                title = "Leitura & Estudos",
                pillar = PillarType.READING,
                subtitle = "Alimentação intelectual diária."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ReadingOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.readingOption == option,
                            activeColor = PillarReading,
                            onClick = { viewModel.setReadingOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Equilíbrio: 15 a 45 min diários. Acima de 2h ininterruptas satura o foco.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. BLOCO LAZER REAL
        if (activePillars.contains(PillarType.LEISURE)) {
            CheckInBlockCard(
                title = "Lazer Real & Descompressão",
                pillar = PillarType.LEISURE,
                subtitle = "Atividades intencionais (séries, jogos, passeios, hobbies)."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LeisureOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.leisureOption == option,
                            activeColor = PillarLeisure,
                            onClick = { viewModel.setLeisureOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Equilíbrio: 1h a 3h por dia. >5h canibaliza sono e projetos.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. BLOCO RELAÇÕES & CONEXÃO
        if (activePillars.contains(PillarType.RELATIONS)) {
            CheckInBlockCard(
                title = "Relações & Conexão Social",
                pillar = PillarType.RELATIONS,
                subtitle = "Presença afetiva e contato com quem importa."
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SocialOption.values().forEach { option ->
                        val isSelected = draftInput.socialOption == option
                        Surface(
                            onClick = { viewModel.setSocialOption(option) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) PillarRelations else DarkBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setSocialOption(option) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PillarRelations,
                                        unselectedColor = TextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = "${option.detail} (${option.points} pts)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 6. BLOCO CRIAÇÃO & PROJETOS MAKER
        if (activePillars.contains(PillarType.CREATION)) {
            CheckInBlockCard(
                title = "Criação & Projetos Maker",
                pillar = PillarType.CREATION,
                subtitle = "Robótica, programação de jogos, pintura, arte e projetos autorais."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CreationOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.creationOption == option,
                            activeColor = PillarCreation,
                            onClick = { viewModel.setCreationOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("1h a 2h de criação coloca o cérebro em estado de Flow realizador.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 7. BLOCO FINANÇAS & CONTROLE
        if (activePillars.contains(PillarType.FINANCE)) {
            CheckInBlockCard(
                title = "Finanças & Prosperidade",
                pillar = PillarType.FINANCE,
                subtitle = "Gestão consciente de gastos, investimentos e segurança orçamentária."
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FinanceOption.values().forEach { option ->
                        PillButton(
                            text = option.label,
                            isSelected = draftInput.financeOption == option,
                            activeColor = if (option == FinanceOption.IMPULSIVE) OverloadRed else PillarFinance,
                            onClick = { viewModel.setFinanceOption(option) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Evitar compras impulsivas e manter aportes em dia garante paz de espírito.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 8. BLOCO PROPÓSITO & EXISTENCIALISMO (NOVO)
        if (activePillars.contains(PillarType.PURPOSE)) {
            CheckInBlockCard(
                title = "Propósito & Existencialismo",
                pillar = PillarType.PURPOSE,
                subtitle = "Clareza de sentido, valores e ações intencionais no dia a dia."
            ) {
                Text(
                    text = "Como você avalia seu alinhamento com seu propósito hoje?",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PurposeOption.values().forEach { option ->
                        val isSelected = draftInput.purposeOption == option
                        Surface(
                            onClick = { viewModel.setPurposeOption(option) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) PillarPurpose.copy(alpha = 0.22f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                if (isSelected) 1.5.dp else 1.dp,
                                if (isSelected) PillarPurpose else DarkBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("purpose_option_${option.name.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { viewModel.setPurposeOption(option) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PillarPurpose,
                                        unselectedColor = TextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) PillarPurpose else TextPrimary
                                        )
                                    )
                                    Text(
                                        text = option.detail,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HelperText("Ter clareza do porquê você faz o que faz transforma rotinas em realizações com sentido.")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

            // 6. BLOCO RUÍDO DIGITAL
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, DigitalNoisePurple.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("digital_noise_block")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(DigitalNoisePurple.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhonelinkErase,
                                contentDescription = null,
                                tint = DigitalNoisePurple,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ruído Digital (Redes & Feeds)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Tempo passivo em timelines. Redutor dos outros pilares.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DigitalNoiseOption.values().forEach { option ->
                            val isSelected = draftInput.digitalNoiseOption == option
                            val activePillColor = if (option == DigitalNoiseOption.LESS_20_MIN)
                                EmeraldHarmony
                            else if (option == DigitalNoiseOption.MIN_30_TO_1H)
                                GoldPrimary
                            else
                                OverloadRed

                            Surface(
                                onClick = { viewModel.setDigitalNoiseOption(option) },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) activePillColor.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) activePillColor else DarkBorder
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 2.dp)
                                ) {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) activePillColor else TextPrimary,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = draftInput.digitalNoiseOption.detail,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (draftInput.digitalNoiseOption.isCleanMindBuff) EmeraldLight else TextSecondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun CheckInBlockCard(
    title: String,
    pillar: PillarType,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, pillar.color.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(pillar.color.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (pillar) {
                        PillarType.WORK -> Icons.Default.Work
                        PillarType.HEALTH -> Icons.Default.Favorite
                        PillarType.CREATION -> Icons.Default.Build
                        PillarType.FINANCE -> Icons.Default.AccountBalanceWallet
                        PillarType.READING -> Icons.Default.MenuBook
                        PillarType.LEISURE -> Icons.Default.SportsEsports
                        PillarType.RELATIONS -> Icons.Default.People
                        PillarType.PURPOSE -> Icons.Default.AutoAwesome
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = pillar.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun PillButton(
    text: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) activeColor.copy(alpha = 0.22f) else DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) activeColor else DarkBorder
        ),
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) activeColor else TextPrimary,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun <T> SubSelectorRow(
    label: String,
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (option, optionLabel) ->
                val isSelected = selected == option
                Surface(
                    onClick = { onSelect(option) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) EmeraldDark.copy(alpha = 0.3f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) EmeraldHarmony else DarkBorder
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 2.dp)
                    ) {
                        Text(
                            text = optionLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) EmeraldLight else TextSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    )
}
