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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhilosophyGuideSheet(
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSurfaceElevated,
        dragHandle = { BottomSheetDefaults.DragHandle(color = TextMuted) },
        modifier = Modifier.testTag("philosophy_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(EmeraldHarmony.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = null,
                        tint = EmeraldHarmony,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "A Filosofia do Equilibrium",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                    Text(
                        text = "Harmonia dinâmica vs. Produtividade tóxica",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PhilosophyTopicCard(
                icon = Icons.Default.Adjust,
                iconTint = EmeraldHarmony,
                title = "1. A Zona Dourada (40 a 70 pts)",
                description = "Ao contrário de apps lineares que celebram bater 100% a qualquer custo, o Equilibrium busca o centro saudável (Target = 60 pts). O excesso em qualquer área canibaliza as outras."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhilosophyTopicCard(
                icon = Icons.Default.Warning,
                iconTint = OverloadRed,
                title = "2. Penalidades Cruzadas",
                description = "Trabalhar 10h ou 12h drena automaticamente pontos de Saúde e Relações. Da mesma forma, 5h+ de telas passivas destroem o sono e anulam o tempo de leitura."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhilosophyTopicCard(
                icon = Icons.Default.PhonelinkErase,
                iconTint = DigitalNoisePurple,
                title = "3. O Ruído Digital (Redes & Feeds)",
                description = "O feed passivo não é lazer genuíno; é um dreno. Menos de 20 min diários concede o buff 'Mente Limpa' (+15% XP). Acima de 1h ativa a névoa visual de dispersão."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhilosophyTopicCard(
                icon = Icons.Default.Bolt,
                iconTint = GoldPrimary,
                title = "4. Micro-Quests Anti-Burnout",
                description = "A cada check-in noturno, o sistema calcula sua área de maior distorção e prescreve uma única ação pontual para recuperar o equilíbrio no dia seguinte."
            )

            Spacer(modifier = Modifier.height(12.dp))

            PhilosophyTopicCard(
                icon = Icons.Default.Build,
                iconTint = PillarCreation,
                title = "5. Criação Autoral & Finanças",
                description = "Construir robôs, criar jogos, pintar ou programar coloca a mente no estado de Flow criativo. Já a tranquilidade financeira protege contra o estresse invisível, mantendo o ecossistema estável."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldHarmony, contentColor = Color.Black),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Entendido, focar no Equilíbrio", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhilosophyTopicCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                )
            }
        }
    }
}
