package com.example.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EquilibriumViewModel

@Composable
fun SettingsResetDialog(
    viewModel: EquilibriumViewModel,
    onDismiss: () -> Unit
) {
    var showConfirmReset by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .testTag("settings_reset_dialog")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(GoldPrimary.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Configuração & Reset",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Calibração e controle de dados",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                showSuccessMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldDark.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldHarmony,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodySmall.copy(color = EmeraldLight)
                            )
                        }
                    }
                }

                // Opção: Exportar Backup (JSON)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PillarWork.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = PillarWork,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exportar Backup Completo (JSON)",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Gera um arquivo .json com todos os seus check-ins dos 7 pilares e metas mensais, abrindo o menu de compartilhamento do Android.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.exportDataToJson(context)
                                showSuccessMessage = "Arquivo JSON gerado e pronto para envio/salvamento!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PillarWork.copy(alpha = 0.2f),
                                contentColor = PillarWork
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PillarWork),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("export_backup_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exportar Arquivo JSON",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opção 1: Zerar Aplicativo
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = OverloadRed.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OverloadRed.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                contentDescription = null,
                                tint = OverloadRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Zerar Aplicativo",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5)
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Limpa todo o histórico de check-ins, pontuações salvas e quests concluídas. O aplicativo volta ao estado inicial limpo.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showConfirmReset = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OverloadRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reset_app_button")
                        ) {
                            Text(
                                text = "Zerar Todos os Dados",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opção 2: Calibrar Padrão Inicial
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = EmeraldHarmony,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Calibrar Padrão de Início",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Define os valores iniciais da sua rotina ideal (acordar 5h-6h30, dormir <23h, 8h trabalho, 30 min leitura).",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val standardBaseline = DailyCheckInInput(
                                    isDayOff = false,
                                    workOption = WorkOption.H8,
                                    bedtimeOption = BedtimeOption.BEFORE_23,
                                    wakeTimeOption = WakeTimeOption.EARLY_DAWN,
                                    sleepOption = SleepOption.HOURS_7_8,
                                    hydrationOption = HydrationOption.REGULAR,
                                    nutritionOption = NutritionOption.BALANCED,
                                    movementOption = MovementOption.MIN_30_45,
                                    readingOption = ReadingOption.MIN_30,
                                    leisureOption = LeisureOption.H2_3,
                                    socialOption = SocialOption.ROUTINE,
                                    digitalNoiseOption = DigitalNoiseOption.LESS_20_MIN
                                )
                                viewModel.calibrateInitialBaseline(standardBaseline)
                                showSuccessMessage = "Padrão inicial calibrado com disciplina circadiana!"
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceElevated,
                                contentColor = EmeraldHarmony
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldHarmony.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calibrate_baseline_button")
                        ) {
                            Text(
                                text = "Calibrar com Disciplina Matinal",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Opção 3: Restaurar Dados de Demonstração
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoGraph,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Restaurar Dados de Exemplo",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Popula o radar e o histórico com dias simulados de harmonia dinâmica para demonstração.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.restoreDemoData {
                                    showSuccessMessage = "Dados de exemplo restaurados com sucesso!"
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("restore_demo_button")
                        ) {
                            Text("Restaurar Demonstração", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Fechar", color = TextSecondary)
                }
            }
        }
    }

    // Confirmation Alert for Reset
    if (showConfirmReset) {
        AlertDialog(
            onDismissRequest = { showConfirmReset = false },
            title = {
                Text(
                    text = "Zerar Aplicativo?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Você tem certeza de que deseja apagar todos os registros, históricos e progresso? Esta ação não pode ser desfeita.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAppToZero {
                            showConfirmReset = false
                            showSuccessMessage = "Aplicativo zerado com sucesso! Comece seu primeiro check-in."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OverloadRed)
                ) {
                    Text("Sim, Zerar Tudo", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmReset = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = DarkSurfaceElevated
        )
    }
}
