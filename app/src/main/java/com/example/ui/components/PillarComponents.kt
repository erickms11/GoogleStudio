package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MicroQuest
import com.example.model.PillarScoreDetail
import com.example.model.PillarType
import com.example.model.PillarZone
import com.example.ui.theme.*

@Composable
fun HarmonyScoreBanner(
    harmonyScore: Int,
    statusTitle: String,
    statusSubtitle: String,
    activeBuffs: List<String>,
    crossPenalties: List<String>,
    modifier: Modifier = Modifier
) {
    val scoreColor = when {
        harmonyScore >= 80 -> EmeraldHarmony
        harmonyScore >= 65 -> GoldPrimary
        harmonyScore >= 50 -> DeficiencyAmber
        else -> OverloadRed
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("harmony_score_banner")
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(scoreColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusTitle.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = scoreColor
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = statusSubtitle,
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                // Circular score display
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(scoreColor.copy(alpha = 0.12f))
                        .border(2.dp, scoreColor.copy(alpha = 0.7f), CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$harmonyScore%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = scoreColor
                            )
                        )
                        Text(
                            text = "HARMONIA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                        )
                    }
                }
            }

            // Buffs and Cross-Penalties tags
            if (activeBuffs.isNotEmpty() || crossPenalties.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    activeBuffs.forEach { buff ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldDark.copy(alpha = 0.25f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.5f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldHarmony,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = buff,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldLight,
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    crossPenalties.forEach { penalty ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = OverloadRed.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, OverloadRed.copy(alpha = 0.4f))
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = OverloadRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = penalty,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color(0xFFFCA5A5),
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
}

@Composable
fun MicroQuestCard(
    quest: MicroQuest,
    onToggleComplete: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (quest.isCompleted) DarkSurfaceVariant.copy(alpha = 0.6f) else DarkSurfaceElevated
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (quest.isCompleted) EmeraldHarmony.copy(alpha = 0.6f) else GoldPrimary.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("micro_quest_card")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Checkbox(
                checked = quest.isCompleted,
                onCheckedChange = onToggleComplete,
                colors = CheckboxDefaults.colors(
                    checkedColor = EmeraldHarmony,
                    checkmarkColor = Color.Black,
                    uncheckedColor = TextMuted
                ),
                modifier = Modifier.testTag("micro_quest_checkbox")
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MICRO-QUEST DE COMPENSAÇÃO",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (quest.isCompleted) EmeraldHarmony else GoldPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = GoldDark.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = "+${quest.xpReward} XP",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldLight
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = quest.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (quest.isCompleted) TextMuted else TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = quest.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
fun PillarStatusCard(
    detail: PillarScoreDetail,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pillar = detail.pillar
    val zone = detail.zone

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) pillar.color else DarkBorder
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("pillar_card_${pillar.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
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
                        contentDescription = pillar.title,
                        tint = pillar.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pillar.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        ),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = detail.note,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        ),
                        softWrap = true
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.widthIn(min = 64.dp)
                ) {
                    Text(
                        text = "${detail.score} pts",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = zone.color
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = zone.color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = zone.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = zone.color
                            ),
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isSelected) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = DarkBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (zone) {
                            PillarZone.DEFICIENCY -> "Zona de Deficiência: requer atenção consciente e pequenas ações corretivas diárias para restaurar a vitalidade deste pilar."
                            PillarZone.GOLDEN_ZONE -> "Zona Dourada (40 - 70): estado ótimo de harmonia e consistência sem excessos prejudiciais."
                            PillarZone.OVERLOAD -> "Zona de Sobrecarga: intensidade excessiva com risco de fadiga ou descompensação em outros pilares de vida."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = zone.color,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }
            }
        }
    }
}
