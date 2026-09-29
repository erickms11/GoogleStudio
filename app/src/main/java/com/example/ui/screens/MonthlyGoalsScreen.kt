package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GoalMetricProgress
import com.example.model.MonthlyGoals
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyGoalsScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val goalsProgress by viewModel.goalsProgress.collectAsState()
    val monthlyGoals by viewModel.monthlyGoals.collectAsState()
    var showEditGoalsDialog by remember { mutableStateOf(false) }

    val achievedCount = remember(goalsProgress) {
        goalsProgress.count { it.isAchieved }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Metas & Objetivos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Acompanhamento mensal de consistência",
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
                        modifier = Modifier.testTag("goals_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showEditGoalsDialog = true },
                        modifier = Modifier.testTag("edit_goals_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Metas",
                            tint = EmeraldHarmony
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("goals_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Summary Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "CONFORMIDADE MENSAL",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldLight,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "$achievedCount de ${goalsProgress.size} metas atingidas",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(EmeraldDark.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldHarmony,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Estas metas são parâmetros pessoais para manter hábitos equilibrados a longo prazo, sem cobranças punitivas de XP.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "SEUS OBJETIVOS EM ANDAMENTO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(goalsProgress) { goal ->
                GoalProgressCard(goal = goal)
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showEditGoalsDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = EmeraldHarmony
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ajustar Valores das Metas",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showEditGoalsDialog) {
        EditGoalsDialog(
            currentGoals = monthlyGoals,
            onSave = { updated ->
                viewModel.saveMonthlyGoals(updated)
                showEditGoalsDialog = false
            },
            onDismiss = { showEditGoalsDialog = false }
        )
    }
}

@Composable
private fun GoalProgressCard(goal: GoalMetricProgress) {
    val progressAnimated by animateFloatAsState(
        targetValue = (goal.progressPercent / 100f).coerceIn(0f, 1f),
        label = "progress"
    )

    val statusColor = if (goal.isAchieved) EmeraldHarmony else GoldPrimary

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(statusColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${goal.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(DarkSurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progressAnimated)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(statusColor)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Atual: ${goal.currentFormatted}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = goal.targetFormatted,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "• ${goal.motivationalTip}",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun EditGoalsDialog(
    currentGoals: MonthlyGoals,
    onSave: (MonthlyGoals) -> Unit,
    onDismiss: () -> Unit
) {
    var sleepHours by remember { mutableStateOf(currentGoals.targetSleepHours) }
    var readingMinutes by remember { mutableStateOf(currentGoals.targetReadingMinutesDaily) }
    var leisureHours by remember { mutableStateOf(currentGoals.targetLeisureHoursDaily) }
    var workHours by remember { mutableStateOf(currentGoals.targetWorkHoursDaily) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Configurar Metas Mensais",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Sono
                GoalSliderItem(
                    label = "Meta de Sono Noturno",
                    valueText = String.format(java.util.Locale.US, "%.1f horas / noite", sleepHours),
                    value = sleepHours,
                    range = 6.0f..9.0f,
                    steps = 5,
                    onValueChange = { sleepHours = it }
                )

                // Leitura
                GoalSliderItem(
                    label = "Meta de Leitura Diária",
                    valueText = "$readingMinutes minutos / dia",
                    value = readingMinutes.toFloat(),
                    range = 10f..90f,
                    steps = 7,
                    onValueChange = { readingMinutes = it.toInt() }
                )

                // Lazer
                GoalSliderItem(
                    label = "Meta de Lazer Real",
                    valueText = String.format(java.util.Locale.US, "%.1f horas / dia", leisureHours),
                    value = leisureHours,
                    range = 1.0f..4.0f,
                    steps = 5,
                    onValueChange = { leisureHours = it }
                )

                // Trabalho
                GoalSliderItem(
                    label = "Teto Máximo de Trabalho",
                    valueText = String.format(java.util.Locale.US, "%.1f horas / dia", workHours),
                    value = workHours,
                    range = 6.0f..10.0f,
                    steps = 7,
                    onValueChange = { workHours = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        currentGoals.copy(
                            targetSleepHours = sleepHours,
                            targetReadingMinutesDaily = readingMinutes,
                            targetLeisureHoursDaily = leisureHours,
                            targetWorkHoursDaily = workHours
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldHarmony, contentColor = Color.Black)
            ) {
                Text("Salvar Metas", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextSecondary)
            }
        },
        containerColor = DarkSurfaceElevated
    )
}

@Composable
private fun GoalSliderItem(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    steps: Int,
    onValueChange: (Float) -> Unit
) {
    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = EmeraldLight
                )
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = EmeraldHarmony,
                activeTrackColor = EmeraldHarmony,
                inactiveTrackColor = DarkSurfaceVariant
            )
        )
    }
}
