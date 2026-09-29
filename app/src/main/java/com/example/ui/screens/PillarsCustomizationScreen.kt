package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PillarType
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillarsCustomizationScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val activePillars by viewModel.activePillars.collectAsState()
    val allPillars = remember { PillarType.entries }
    val activeCount = activePillars.size
    val weightPerPillar = if (activeCount > 0) 100f / activeCount else 0f

    var showMinPillarsWarning by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "GERENCIAR PILARES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Personalize as dimensões ativas da sua vida",
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
                        modifier = Modifier.testTag("btn_back_pillars_customization")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.resetActivePillars() },
                        modifier = Modifier.testTag("btn_reset_all_pillars")
                    ) {
                        Text(
                            text = "Restaurar (8/8)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = EmeraldHarmony,
                                fontWeight = FontWeight.SemiBold
                            )
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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Info Banner
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "COMO FUNCIONA O CÁLCULO DINÂMICO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GoldPrimary,
                                    letterSpacing = 1.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Pilares desativados são completamente removidos do cálculo de harmonia e das perguntas do check-in diário. Os 100% da fórmula são redistribuídos igualmente entre os pilares ativos.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EmeraldHarmony.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "$activeCount de 8 pilares ativos",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldHarmony,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Text(
                                text = "Peso atual: ~${String.format("%.1f", weightPerPillar)}% cada",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            if (showMinPillarsWarning) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = OverloadRed.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, OverloadRed.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = OverloadRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mínimo de 2 pilares ativos para manter a consistência matemática da harmonia.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = OverloadRed,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }

            items(allPillars, key = { it.name }) { pillar ->
                val isActive = activePillars.contains(pillar)

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isActive) DarkSurfaceElevated else DarkSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isActive) pillar.color.copy(alpha = 0.5f) else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("pillar_item_${pillar.name.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(
                                    if (isActive) pillar.color else Color.DarkGray,
                                    CircleShape
                                )
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = pillar.title,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) TextPrimary else TextMuted
                                    )
                                )
                                if (pillar == PillarType.PURPOSE) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = PillarPurpose.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, PillarPurpose.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "NOVO",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = PillarPurpose,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = pillar.description,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isActive) TextSecondary else TextMuted,
                                    fontSize = 11.5.sp,
                                    lineHeight = 15.sp
                                )
                            )

                            if (isActive) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Participação no Score: ~${String.format("%.1f", weightPerPillar)}%",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = pillar.color,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Switch(
                            checked = isActive,
                            onCheckedChange = { checked ->
                                if (!checked && activeCount <= 2) {
                                    showMinPillarsWarning = true
                                } else {
                                    showMinPillarsWarning = false
                                    viewModel.togglePillar(pillar)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = pillar.color,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.testTag("switch_pillar_${pillar.name.lowercase()}")
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
