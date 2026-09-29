package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CheckInEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val allCheckIns by viewModel.allCheckIns.collectAsState()

    val averageScore = remember(allCheckIns) {
        if (allCheckIns.isNotEmpty()) {
            allCheckIns.map { it.harmonyScore }.average().toInt()
        } else 0
    }

    val goldenZoneDays = remember(allCheckIns) {
        allCheckIns.count { it.harmonyScore >= 70 }
    }

    val completedQuestsCount = remember(allCheckIns) {
        allCheckIns.count { it.questCompleted }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Histórico de Harmonia",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "${allCheckIns.size} registros arquivados",
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
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
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
                .testTag("history_list")
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Weekly Summary Metric Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MetricSummaryCard(
                        title = "Média Geral",
                        value = "$averageScore%",
                        color = if (averageScore >= 70) EmeraldHarmony else GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryCard(
                        title = "Zona Dourada",
                        value = "$goldenZoneDays dias",
                        color = EmeraldHarmony,
                        modifier = Modifier.weight(1f)
                    )
                    MetricSummaryCard(
                        title = "Quests Concluídas",
                        value = "$completedQuestsCount",
                        color = PillarReading,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "REGISTROS ANTERIORES",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (allCheckIns.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum check-in registrado ainda",
                                style = MaterialTheme.typography.titleSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            } else {
                items(allCheckIns, key = { it.id }) { checkIn ->
                    HistoryItemCard(
                        checkIn = checkIn,
                        onToggleQuest = { completed ->
                            viewModel.toggleQuestCompleted(checkIn.id, completed)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun MetricSummaryCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    checkIn: CheckInEntity,
    onToggleQuest: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val scoreColor = when {
        checkIn.harmonyScore >= 80 -> EmeraldHarmony
        checkIn.harmonyScore >= 65 -> GoldPrimary
        checkIn.harmonyScore >= 50 -> DeficiencyAmber
        else -> OverloadRed
    }

    val displayDate = remember(checkIn.timestamp) {
        val sdf = SimpleDateFormat("dd 'de' MMMM", Locale.forLanguageTag("pt-BR"))
        sdf.format(Date(checkIn.timestamp))
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = displayDate,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "${checkIn.statusTitle} • Ruído: ${checkIn.digitalNoiseOptionLabel}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = scoreColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${checkIn.harmonyScore}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = scoreColor
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Pillar chips preview
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PillarPill(label = "Trab", score = checkIn.workScore, color = PillarWork, modifier = Modifier.weight(1f))
                PillarPill(label = "Saúd", score = checkIn.healthScore, color = PillarHealth, modifier = Modifier.weight(1f))
                PillarPill(label = "Leit", score = checkIn.readingScore, color = PillarReading, modifier = Modifier.weight(1f))
                PillarPill(label = "Laze", score = checkIn.leisureScore, color = PillarLeisure, modifier = Modifier.weight(1f))
                PillarPill(label = "Rela", score = checkIn.socialScore, color = PillarRelations, modifier = Modifier.weight(1f))
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = DarkBorder, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "MICRO-QUEST ASSOCIADA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary,
                            fontSize = 9.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checkIn.questCompleted,
                            onCheckedChange = onToggleQuest,
                            colors = CheckboxDefaults.colors(
                                checkedColor = EmeraldHarmony,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = checkIn.questTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (checkIn.questCompleted) TextMuted else TextPrimary
                                )
                            )
                            Text(
                                text = checkIn.questDescription,
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
}

@Composable
private fun PillarPill(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    color = TextMuted
                )
            )
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}
