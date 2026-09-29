package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DisciplineMetric
import com.example.ui.theme.*

@Composable
fun DisciplineHabitTrackerCard(
    metric: DisciplineMetric,
    onToggleSleepEarly: (Boolean) -> Unit,
    onToggleWakeEarly: (Boolean) -> Unit,
    onNavigateToCheckIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        metric.score == 100 -> EmeraldHarmony
        metric.score >= 50 -> GoldPrimary
        else -> DeficiencyAmber
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, scoreColor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("discipline_habit_card")
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header with Title and Streak badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.25f), EmeraldHarmony.copy(alpha = 0.25f))),
                                CircleShape
                            )
                            .border(1.dp, GoldPrimary.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DISCIPLINA CIRCADIANA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Rastreador de hábitos solares",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Streak Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GoldPrimary.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${metric.streakDays} ${if (metric.streakDays == 1) "dia" else "dias"}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score and Progress summary
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                // Circular percentage badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(scoreColor.copy(alpha = 0.15f))
                        .border(1.5.dp, scoreColor, CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${metric.score}%",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = scoreColor
                            )
                        )
                        Text(
                            text = "ÍNDICE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = metric.statusLabel,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = scoreColor
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Aderência semanal: ${metric.weeklyAdherence}% • 2 hábitos ativos",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Habits Checklist: Dormir Cedo e Acordar Cedo
            Text(
                text = "HÁBITOS DO CICLO SOLAR",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Habit 1: Dormir Cedo (< 23h)
            HabitToggleRow(
                title = "Dormir Cedo (< 23h)",
                subtitle = if (metric.sleptEarly) "Sono restaurador profundo ativado" else "Último registro: ${metric.bedtimeLabel}",
                isCompleted = metric.sleptEarly,
                icon = Icons.Default.Bedtime,
                activeColor = EmeraldHarmony,
                onToggle = { onToggleSleepEarly(!metric.sleptEarly) },
                testTag = "habit_sleep_early"
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Habit 2: Acordar Cedo (5h - 6h30)
            HabitToggleRow(
                title = "Acordar Cedo (5h - 6h30)",
                subtitle = if (metric.wokeEarly) "Alvorada e alinhamento com a luz solar" else "Último registro: ${metric.wakeTimeLabel}",
                isCompleted = metric.wokeEarly,
                icon = Icons.Default.WbSunny,
                activeColor = GoldPrimary,
                onToggle = { onToggleWakeEarly(!metric.wokeEarly) },
                testTag = "habit_wake_early"
            )

            // Buff banner or calibration guidance
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = metric.score == 100) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldDark.copy(alpha = 0.25f),
                    border = BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EmeraldHarmony,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Buff Ativo: +5 pts Vigor & Foco Mental em Saúde",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = EmeraldHarmony,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp
                            )
                        )
                    }
                }
            }

            AnimatedVisibility(visible = metric.score < 100) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onNavigateToCheckIn)
                        .padding(vertical = 4.dp, horizontal = 2.dp)
                ) {
                    Text(
                        text = "Complete o check-in noturno para registrar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "Calibrar →",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun HabitToggleRow(
    title: String,
    subtitle: String,
    isCompleted: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    activeColor: Color,
    onToggle: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(12.dp),
        color = if (isCompleted) activeColor.copy(alpha = 0.12f) else DarkSurfaceVariant,
        border = BorderStroke(
            1.dp,
            if (isCompleted) activeColor.copy(alpha = 0.45f) else DarkBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (isCompleted) activeColor.copy(alpha = 0.2f) else DarkSurfaceElevated,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isCompleted) activeColor else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) TextPrimary else TextSecondary,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 10.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                contentDescription = if (isCompleted) "Cumprido" else "Pendente",
                tint = if (isCompleted) activeColor else DarkBorder,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
