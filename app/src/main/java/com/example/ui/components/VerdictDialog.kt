package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.HarmonyResult
import com.example.ui.theme.*

@Composable
fun VerdictDialog(
    result: HarmonyResult,
    onDismiss: () -> Unit
) {
    val animatedScore = remember { Animatable(0f) }

    LaunchedEffect(result.harmonyScore) {
        animatedScore.animateTo(
            targetValue = result.harmonyScore.toFloat(),
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val scoreColor = when {
        result.harmonyScore >= 80 -> EmeraldHarmony
        result.harmonyScore >= 65 -> GoldPrimary
        result.harmonyScore >= 50 -> DeficiencyAmber
        else -> OverloadRed
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, scoreColor.copy(alpha = 0.6f)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("verdict_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = scoreColor.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, scoreColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "VEREDITO DO DIA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                            letterSpacing = 1.5.sp
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Score Circle Counter
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(110.dp)
                        .background(scoreColor.copy(alpha = 0.12f), CircleShape)
                        .border(3.dp, scoreColor, CircleShape)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${animatedScore.value.toInt()}%",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = scoreColor,
                                fontSize = 34.sp
                            )
                        )
                        Text(
                            text = "HARMONY SCORE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = result.statusTitle,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )

                Text(
                    text = result.statusSubtitle,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                // Cross Penalties if any
                if (result.crossPenalties.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = OverloadRed.copy(alpha = 0.1f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, OverloadRed.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = OverloadRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Efeitos Cruzados Detectados",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = OverloadRed
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            result.crossPenalties.forEach { penalty ->
                                Text(
                                    text = "• $penalty",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
                                        color = Color(0xFFFCA5A5)
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Micro-Quest Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "MICRO-QUEST DO DIA SEGUINTE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "+${result.microQuest.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldLight
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = result.microQuest.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = result.microQuest.description,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = scoreColor,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("verdict_accept_button")
                ) {
                    Text(
                        text = "Aceitar & Voltar ao Equilíbrio",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}
