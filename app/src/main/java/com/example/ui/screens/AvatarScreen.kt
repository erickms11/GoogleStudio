package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvatarEmotion
import com.example.ui.components.PixelAvatarView
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvatarScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCheckIn: () -> Unit
) {
    val liveResult by viewModel.liveResult.collectAsState()
    val emotion = AvatarEmotion.fromScore(liveResult.harmonyScore)
    val speech = AvatarEmotion.getContextualSpeech(
        score = liveResult.harmonyScore,
        crossPenalties = liveResult.crossPenalties,
        activeBuffs = liveResult.activeBuffs
    )

    var tapCount by remember { mutableIntStateOf(0) }
    val tapResponses = remember(emotion) {
        when (emotion) {
            AvatarEmotion.HAPPY -> listOf(
                "Estou no meu melhor momento! Bora manter esse ritmo.",
                "Corpo em dia, mente focada. Nada me abala hoje!",
                "Você mandou bem no equilíbrio das atividades."
            )
            AvatarEmotion.TIRED -> listOf(
                "Preciso de uma pausa... Meus olhos estão pesados.",
                "Um café ou uma caminhada de 10 minutos me salvaria.",
                "Que tal fechar um pouco as abas do navegador?"
            )
            AvatarEmotion.SAD -> listOf(
                "O desequilíbrio está me desgastando aos poucos.",
                "Não esqueça de beber água e se movimentar...",
                "Vamos cuidar da saúde antes que vire estafa!"
            )
            AvatarEmotion.EXHAUSTED -> listOf(
                "SOCORRO! Minha bateria está em 1%...",
                "Desligue as notificações! Preciso de repouso urgente.",
                "Estafa total. Por favor, priorize seu sono hoje."
            )
        }
    }

    val currentSpeechText = remember(tapCount, speech) {
        if (tapCount == 0) speech else tapResponses[tapCount % tapResponses.size]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AVATAR VITAL",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Espelho emocional do seu equilíbrio",
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
                        modifier = Modifier.testTag("avatar_back_button")
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
        bottomBar = {
            Surface(
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, DarkBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = onNavigateToCheckIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("btn_avatar_checkin"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = emotion.primaryColor,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.EditCalendar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Calibrar Pilares no Check-in",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        },
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))

                // Hero Avatar Box
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, emotion.primaryColor.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        // Emotion Tag
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = emotion.primaryColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, emotion.primaryColor.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(emotion.primaryColor, CircleShape)
                                )
                                Text(
                                    text = "${emotion.title.uppercase()} • ${liveResult.harmonyScore}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = emotion.primaryColor,
                                        letterSpacing = 1.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Big Interactive Pixel Avatar
                        PixelAvatarView(
                            emotion = emotion,
                            size = 180.dp,
                            showSpeechBubble = false,
                            onClick = { tapCount++ }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Retro RPG Speech Bubble
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = DarkSurfaceVariant,
                            border = BorderStroke(1.dp, emotion.primaryColor.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("avatar_speech_bubble")
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    tint = emotion.primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = currentSpeechText,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Medium,
                                            lineHeight = 20.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "(Toque no avatar para interagir)",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Emotion status card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "DIAGNÓSTICO VITAL ATUAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                letterSpacing = 1.2.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = emotion.subtitle,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Mini metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MetricPill(
                                label = "Saúde",
                                score = liveResult.healthScore,
                                color = PillarHealth,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Trabalho",
                                score = liveResult.workScore,
                                color = PillarWork,
                                modifier = Modifier.weight(1f)
                            )
                            MetricPill(
                                label = "Lazer",
                                score = liveResult.leisureScore,
                                color = PillarLeisure,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Active Buffs or Critical Warnings
            if (liveResult.crossPenalties.isNotEmpty() || liveResult.activeBuffs.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                        border = BorderStroke(1.dp, DarkBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "FATORES QUE MOLDARAM O ROSTO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary,
                                    letterSpacing = 1.2.sp
                                )
                            )

                            liveResult.crossPenalties.forEach { penalty ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = OverloadRed.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, OverloadRed.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = OverloadRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = penalty,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = OverloadRed,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                            }

                            liveResult.activeBuffs.forEach { buff ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EmeraldHarmony.copy(alpha = 0.1f),
                                    border = BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = EmeraldHarmony,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = buff,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = EmeraldHarmony,
                                                fontSize = 11.5.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceVariant,
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "$score",
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
        }
    }
}
