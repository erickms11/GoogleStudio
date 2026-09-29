package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvatarEmotion
import com.example.model.MicroQuest
import com.example.model.PillarType
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: EquilibriumViewModel,
    onNavigateToCheckIn: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToGoals: () -> Unit,
    onNavigateToWeights: () -> Unit = {},
    onNavigateToAvatar: () -> Unit = {},
    onNavigateToPillarsCustomization: () -> Unit = {}
) {
    val latestCheckIn by viewModel.latestCheckIn.collectAsState()
    val liveResult by viewModel.liveResult.collectAsState()
    val activePillars by viewModel.activePillars.collectAsState()
    val selectedPillar by viewModel.selectedPillar.collectAsState()
    val showVerdictDialog by viewModel.showVerdictDialog.collectAsState()
    val lastSavedResult by viewModel.lastSavedResult.collectAsState()
    val showPhilosophyGuide by viewModel.showPhilosophyGuide.collectAsState()
    val allCheckIns by viewModel.allCheckIns.collectAsState()
    var showExportConfirmation by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }

    // Determine current active scores from latest check-in or live preview
    val currentWork = latestCheckIn?.workScore ?: liveResult.workScore
    val currentHealth = latestCheckIn?.healthScore ?: liveResult.healthScore
    val currentCreation = latestCheckIn?.creationScore ?: liveResult.creationScore
    val currentFinance = latestCheckIn?.financeScore ?: liveResult.financeScore
    val currentReading = latestCheckIn?.readingScore ?: liveResult.readingScore
    val currentLeisure = latestCheckIn?.leisureScore ?: liveResult.leisureScore
    val currentSocial = latestCheckIn?.socialScore ?: liveResult.socialScore
    val currentPurpose = latestCheckIn?.purposeScore ?: liveResult.purposeScore
    val currentHarmonyScore = latestCheckIn?.harmonyScore ?: liveResult.harmonyScore
    val currentStatusTitle = latestCheckIn?.statusTitle ?: liveResult.statusTitle
    val currentStatusSubtitle = liveResult.statusSubtitle
    val hasDigitalNoiseFog = latestCheckIn?.digitalNoisePenaltyR?.let { it >= 15 } ?: liveResult.hasDigitalNoiseFog

    val activeQuest = remember(latestCheckIn, liveResult) {
        if (latestCheckIn != null) {
            MicroQuest(
                id = latestCheckIn!!.questId,
                title = latestCheckIn!!.questTitle,
                description = latestCheckIn!!.questDescription,
                targetPillar = try { PillarType.valueOf(latestCheckIn!!.questTargetPillar) } catch (_: Exception) { PillarType.HEALTH },
                xpReward = latestCheckIn!!.questXpReward,
                isCompleted = latestCheckIn!!.questCompleted
            )
        } else {
            liveResult.microQuest
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(EmeraldHarmony.copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, EmeraldHarmony, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(GoldPrimary, CircleShape)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EQUILIBRIUM",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            ),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToGoals,
                        modifier = Modifier.testTag("goals_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = "Metas Mensais",
                            tint = EmeraldHarmony
                        )
                    }
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Histórico",
                            tint = TextSecondary
                        )
                    }
                    Box {
                        IconButton(
                            onClick = { showMoreMenu = true },
                            modifier = Modifier.testTag("more_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Mais Opções",
                                tint = TextSecondary
                            )
                        }

                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false },
                            modifier = Modifier.background(DarkSurfaceElevated)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Exportar Backup (JSON)",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = null,
                                        tint = PillarWork
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    showExportConfirmation = true
                                },
                                modifier = Modifier.testTag("menu_export_data")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Filosofia do Equilíbrio",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = GoldPrimary
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.setShowPhilosophyGuide(true)
                                },
                                modifier = Modifier.testTag("menu_philosophy")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Calibrar Pesos & Penalidades",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        tint = EmeraldHarmony
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onNavigateToWeights()
                                },
                                modifier = Modifier.testTag("menu_weights_calibration")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Espelho do Avatar Vital",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Face,
                                        contentDescription = null,
                                        tint = GoldPrimary
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onNavigateToAvatar()
                                },
                                modifier = Modifier.testTag("menu_avatar_mirror")
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Gerenciar Pilares Ativos",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Checklist,
                                        contentDescription = null,
                                        tint = EmeraldHarmony
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    onNavigateToPillarsCustomization()
                                },
                                modifier = Modifier.testTag("menu_pillars_customization")
                            )

                            HorizontalDivider(color = DarkBorder)

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Configuração & Reset",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = TextSecondary
                                    )
                                },
                                onClick = {
                                    showMoreMenu = false
                                    viewModel.setShowSettingsDialog(true)
                                },
                                modifier = Modifier.testTag("menu_settings")
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCheckIn,
                containerColor = EmeraldHarmony,
                contentColor = Color.Black,
                shape = RoundedCornerShape(18.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "Fazer Check-in do Dia",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("checkin_fab")
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("dashboard_scroll")
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Avatar Vital Hero Widget (Retro Pixel Art)
                val avatarEmotion = AvatarEmotion.fromScore(currentHarmonyScore)
                val avatarSpeech = AvatarEmotion.getContextualSpeech(
                    score = currentHarmonyScore,
                    crossPenalties = liveResult.crossPenalties,
                    activeBuffs = liveResult.activeBuffs
                )

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, avatarEmotion.primaryColor.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToAvatar)
                        .testTag("dashboard_avatar_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PixelAvatarView(
                            emotion = avatarEmotion,
                            size = 60.dp,
                            showSpeechBubble = false
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(avatarEmotion.primaryColor, CircleShape)
                                )
                                Text(
                                    text = "AVATAR VITAL: ${avatarEmotion.title.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = avatarEmotion.primaryColor,
                                        letterSpacing = 0.8.sp,
                                        fontSize = 10.5.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = avatarSpeech,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextPrimary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = "Toque para ver o espelho em tela cheia ↗",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Expandir Avatar",
                            tint = avatarEmotion.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Status Banner
                HarmonyScoreBanner(
                    harmonyScore = currentHarmonyScore,
                    statusTitle = currentStatusTitle,
                    statusSubtitle = currentStatusSubtitle,
                    activeBuffs = liveResult.activeBuffs,
                    crossPenalties = liveResult.crossPenalties
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rastreador de Hábitos Diários & Métrica de Disciplina
                val disciplineMetric by viewModel.disciplineMetric.collectAsState()

                DisciplineHabitTrackerCard(
                    metric = disciplineMetric,
                    onToggleSleepEarly = { viewModel.setHabitSleepingEarly(it) },
                    onToggleWakeEarly = { viewModel.setHabitWakingEarly(it) },
                    onNavigateToCheckIn = onNavigateToCheckIn
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Card de Metas Mensais Pessoais
                val goalsProgress by viewModel.goalsProgress.collectAsState()
                val achievedGoalsCount = remember(goalsProgress) { goalsProgress.count { it.isAchieved } }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onNavigateToGoals)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(EmeraldDark.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Flag,
                                contentDescription = null,
                                tint = EmeraldHarmony,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "METAS MENSAIS PESSOAIS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldLight,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$achievedGoalsCount de ${goalsProgress.size} objetivos cumpridos este mês",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Central Radar Chart Section
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "RADAR DE HARMONIA DINÂMICA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Text(
                            text = "Zona Dourada sombreada: 40 a 70 pts",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                color = EmeraldLight
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        EquilibriumRadarChart(
                            workScore = currentWork,
                            healthScore = currentHealth,
                            creationScore = currentCreation,
                            financeScore = currentFinance,
                            readingScore = currentReading,
                            leisureScore = currentLeisure,
                            socialScore = currentSocial,
                            purposeScore = currentPurpose,
                            activePillars = activePillars,
                            hasDigitalNoiseFog = hasDigitalNoiseFog,
                            selectedPillar = selectedPillar,
                            onPillarSelected = { pillar ->
                                if (selectedPillar == pillar) {
                                    viewModel.selectPillar(null)
                                } else {
                                    viewModel.selectPillar(pillar)
                                }
                            }
                        )

                        if (hasDigitalNoiseFog) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DigitalNoisePurple.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DigitalNoisePurple.copy(alpha = 0.5f)),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Grain,
                                        contentDescription = null,
                                        tint = DigitalNoisePurple,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Névoa de Ruído Digital ativa (Tempo de tela passivo elevado)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color(0xFFC7D2FE),
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Selected Pillar Detail Inspector
                AnimatedVisibility(visible = selectedPillar != null) {
                    selectedPillar?.let { pillar ->
                        val detail = liveResult.pillarDetails[pillar]
                        if (detail != null) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, pillar.color),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = pillar.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = pillar.color
                                            )
                                        )
                                        IconButton(
                                            onClick = { viewModel.selectPillar(null) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Fechar",
                                                tint = TextMuted,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = pillar.description,
                                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Score Atual: ${detail.score} pts",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = detail.zone.color
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${detail.zone.label}",
                                            style = MaterialTheme.typography.labelSmall.copy(color = detail.zone.color)
                                        )
                                    }

                                    if (detail.crossPenaltyApplied > 0) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Penalidade cruzada sofrida: -${detail.crossPenaltyApplied} pts",
                                            style = MaterialTheme.typography.labelSmall.copy(color = OverloadRed)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Micro Quest Card
                Text(
                    text = "AÇÃO CORRETIVA DO DIA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                MicroQuestCard(
                    quest = activeQuest,
                    onToggleComplete = { completed ->
                        latestCheckIn?.let {
                            viewModel.toggleQuestCompleted(it.id, completed)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Pillars Breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STATUS DOS PILARES (${activePillars.size} DE 8 ATIVOS)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 1.sp
                        )
                    )
                    TextButton(
                        onClick = onNavigateToPillarsCustomization,
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Personalizar ↗",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = EmeraldHarmony,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // List only active pillars
            val allPillars = listOf(
                PillarType.WORK,
                PillarType.HEALTH,
                PillarType.CREATION,
                PillarType.FINANCE,
                PillarType.PURPOSE,
                PillarType.RELATIONS,
                PillarType.LEISURE,
                PillarType.READING
            )
            val displayedPillars = allPillars.filter { activePillars.contains(it) }

            items(displayedPillars, key = { it.name }) { pillar ->
                val detail = liveResult.pillarDetails[pillar]
                if (detail != null) {
                    PillarStatusCard(
                        detail = detail,
                        isSelected = selectedPillar == pillar,
                        onClick = {
                            viewModel.selectPillar(if (selectedPillar == pillar) null else pillar)
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(88.dp)) // Space for FAB
            }
        }
    }

    // Verdict Dialog when check-in is saved
    if (showVerdictDialog && lastSavedResult != null) {
        VerdictDialog(
            result = lastSavedResult!!,
            onDismiss = { viewModel.setShowVerdictDialog(false) }
        )
    }

    // Philosophy Guide Bottom Sheet
    if (showPhilosophyGuide) {
        PhilosophyGuideSheet(
            onDismiss = { viewModel.setShowPhilosophyGuide(false) }
        )
    }

    // Settings & Reset Dialog
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsState()
    if (showSettingsDialog) {
        SettingsResetDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.setShowSettingsDialog(false) }
        )
    }

    // Export Confirmation Dialog
    if (showExportConfirmation) {
        val context = androidx.compose.ui.platform.LocalContext.current
        val totalCount = allCheckIns.size
        AlertDialog(
            onDismissRequest = { showExportConfirmation = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.FileDownload,
                    contentDescription = null,
                    tint = PillarWork,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Exportar Backup Completo",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Deseja exportar seus dados em formato JSON? O arquivo inclui todo o histórico de check-ins dos 7 pilares e as metas mensais ativas.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "• Total de Check-ins: $totalCount dias registrados",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Formato: Arquivo .json estruturado",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "• Destino: Menu de compartilhamento do Android",
                                style = MaterialTheme.typography.bodySmall.copy(color = EmeraldLight)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExportConfirmation = false
                        viewModel.exportDataToJson(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PillarWork, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exportar Agora", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportConfirmation = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
