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
import com.example.model.ImpactLevel
import com.example.model.PillarWeightsConfig
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightsCalibrationScreen(
    viewModel: EquilibriumViewModel,
    onNavigateBack: () -> Unit
) {
    val currentConfig by viewModel.pillarWeightsConfig.collectAsState()
    var localConfig by remember(currentConfig) { mutableStateOf(currentConfig) }
    var showSavedSnackbar by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PESOS & PENALIDADES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Sensibilidade customizada dos pilares",
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
                        modifier = Modifier.testTag("weights_back_button")
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
                        onClick = { showResetDialog = true },
                        modifier = Modifier.testTag("weights_reset_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Restaurar Padrão",
                            tint = TextSecondary
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            localConfig = PillarWeightsConfig()
                        },
                        modifier = Modifier
                            .weight(0.4f)
                            .height(50.dp)
                            .testTag("btn_reset_defaults"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, DarkBorder)
                    ) {
                        Text(
                            text = "Padrão",
                            style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary)
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.updateWeightsConfig(localConfig)
                            scope.launch {
                                snackbarHostState.showSnackbar("Pesos e penalidades atualizados com sucesso!")
                            }
                        },
                        modifier = Modifier
                            .weight(0.6f)
                            .height(50.dp)
                            .testTag("btn_save_weights"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldHarmony,
                            contentColor = Color.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Salvar Pesos",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    Brush.linearGradient(listOf(GoldPrimary.copy(alpha = 0.2f), EmeraldHarmony.copy(alpha = 0.2f))),
                                    CircleShape
                                )
                                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Calibração de Tolerância",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Defina o rigor do algoritmo. O peso de não trabalhar o suficiente, da sobrecarga de estafa e de descuidar da saúde impactam diretamente o seu Harmony Score.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextSecondary,
                                    fontSize = 11.5.sp,
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }

            // 1. Sub-Trabalho (Não trabalhar o suficiente)
            item {
                WeightControlCard(
                    title = "Sub-Trabalho (Não trabalhar o suficiente)",
                    subtitle = "O quão pesado é não produzir ou trabalhar menos de 6h em dias úteis.",
                    icon = Icons.Default.WorkOff,
                    accentColor = PillarWork,
                    currentLevel = localConfig.workUnderloadImpact,
                    onLevelChange = { localConfig = localConfig.copy(workUnderloadImpact = it) },
                    testTagPrefix = "weight_work_underload"
                )
            }

            // 2. Sobrecarga de Trabalho (Trabalhar demais)
            item {
                WeightControlCard(
                    title = "Sobrecarga de Trabalho (Estafa / Burnout)",
                    subtitle = "O custo corrosivo de trabalhar >10h diárias, canibalizando a Saúde e as Relações.",
                    icon = Icons.Default.WorkHistory,
                    accentColor = OverloadRed,
                    currentLevel = localConfig.workOverloadImpact,
                    onLevelChange = { localConfig = localConfig.copy(workOverloadImpact = it) },
                    testTagPrefix = "weight_work_overload"
                )
            }

            // 3. Negligência de Saúde (Custo de não se cuidar)
            item {
                WeightControlCard(
                    title = "Negligência de Saúde (Custo de não se cuidar)",
                    subtitle = "Penalidade por privação de sono (<6h), desidratação, sedentarismo e má nutrição.",
                    icon = Icons.Default.Favorite,
                    accentColor = PillarHealth,
                    currentLevel = localConfig.healthNeglectImpact,
                    onLevelChange = { localConfig = localConfig.copy(healthNeglectImpact = it) },
                    testTagPrefix = "weight_health_neglect"
                )
            }

            // 4. Ruído Digital (Tempo de tela desnecessário)
            item {
                WeightControlCard(
                    title = "Ruído Digital & Distrações",
                    subtitle = "Penalidade por horas perdidas em feeds inúteis e névoa mental acumulada.",
                    icon = Icons.Default.PhonelinkErase,
                    accentColor = GoldPrimary,
                    currentLevel = localConfig.digitalNoiseImpact,
                    onLevelChange = { localConfig = localConfig.copy(digitalNoiseImpact = it) },
                    testTagPrefix = "weight_digital_noise"
                )
            }

            // 5. Excesso de Lazer em dias úteis
            item {
                WeightControlCard(
                    title = "Excesso de Lazer / Ócio",
                    subtitle = "Penalidade por ócio desproporcional que canibaliza o pilar de Trabalho em dias úteis.",
                    icon = Icons.Default.SportsEsports,
                    accentColor = PillarLeisure,
                    currentLevel = localConfig.leisureOverloadImpact,
                    onLevelChange = { localConfig = localConfig.copy(leisureOverloadImpact = it) },
                    testTagPrefix = "weight_leisure_overload"
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Restaurar Padrões?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Todos os pesos e multiplicadores voltarão ao nível Moderado (1.0x).",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        localConfig = PillarWeightsConfig()
                        viewModel.resetWeightsConfigToDefault()
                        showResetDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Pesos restaurados para o padrão moderado.")
                        }
                    }
                ) {
                    Text("Restaurar", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}

@Composable
private fun WeightControlCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    currentLevel: ImpactLevel,
    onLevelChange: (ImpactLevel) -> Unit,
    testTagPrefix: String
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        border = BorderStroke(1.dp, currentLevel.color.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("${testTagPrefix}_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 13.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Badge of current level
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = currentLevel.color.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, currentLevel.color.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "${currentLevel.label} (${currentLevel.multiplier}x)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = currentLevel.color,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Discrete Slider: 0 = LIGHT, 1 = MODERATE, 2 = STRICT, 3 = EXTREME
            Slider(
                value = currentLevel.sliderPosition,
                onValueChange = { floatVal ->
                    val newIndex = kotlin.math.round(floatVal).toInt()
                    onLevelChange(ImpactLevel.fromIndex(newIndex))
                },
                valueRange = 0f..3f,
                steps = 2, // 2 steps between 0 and 3 gives 4 discrete points
                colors = SliderDefaults.colors(
                    thumbColor = currentLevel.color,
                    activeTrackColor = currentLevel.color,
                    inactiveTrackColor = DarkSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("${testTagPrefix}_slider")
            )

            // Step Labels Row
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ImpactLevel.entries.forEach { level ->
                    val isSelected = level == currentLevel
                    Text(
                        text = "${level.label}\n${level.multiplier}x",
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) level.color else TextMuted
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Description of active level
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = DarkSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentLevel.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = currentLevel.color,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
