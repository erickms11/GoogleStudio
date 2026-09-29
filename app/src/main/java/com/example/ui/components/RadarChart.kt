package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PillarType
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun EquilibriumRadarChart(
    workScore: Int,
    healthScore: Int,
    creationScore: Int = 60,
    financeScore: Int = 60,
    readingScore: Int,
    leisureScore: Int,
    socialScore: Int,
    purposeScore: Int = 60,
    activePillars: Set<PillarType> = PillarType.entries.toSet(),
    hasDigitalNoiseFog: Boolean = false,
    selectedPillar: PillarType? = null,
    onPillarSelected: (PillarType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val allPillarsWithScores = remember(workScore, healthScore, creationScore, financeScore, readingScore, leisureScore, socialScore, purposeScore) {
        listOf(
            PillarType.WORK to workScore.toFloat(),
            PillarType.HEALTH to healthScore.toFloat(),
            PillarType.CREATION to creationScore.toFloat(),
            PillarType.FINANCE to financeScore.toFloat(),
            PillarType.PURPOSE to purposeScore.toFloat(),
            PillarType.RELATIONS to socialScore.toFloat(),
            PillarType.LEISURE to leisureScore.toFloat(),
            PillarType.READING to readingScore.toFloat()
        )
    }

    val activeList = remember(allPillarsWithScores, activePillars) {
        allPillarsWithScores.filter { activePillars.contains(it.first) }
    }

    val pillars = remember(activeList) { activeList.map { it.first } }
    val scores = remember(activeList) { activeList.map { it.second } }

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(scores) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    val fogPulse = remember { Animatable(0.4f) }
    LaunchedEffect(hasDigitalNoiseFog) {
        if (hasDigitalNoiseFog) {
            fogPulse.animateTo(
                targetValue = 0.85f,
                animationSpec = tween(durationMillis = 1200)
            )
        }
    }

    val count = pillars.size

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(315.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 26.dp)
                .pointerInput(pillars) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = (minOf(size.width, size.height) / 2f) * 0.76f
                        var closestPillar: PillarType? = null
                        var minDistance = Float.MAX_VALUE

                        pillars.forEachIndexed { i, pillar ->
                            val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                            val nodeX = center.x + (maxRadius * cos(angle)).toFloat()
                            val nodeY = center.y + (maxRadius * sin(angle)).toFloat()
                            val dist = hypot(tapOffset.x - nodeX, tapOffset.y - nodeY)
                            if (dist < minDistance && dist < 120f) {
                                minDistance = dist
                                closestPillar = pillar
                            }
                        }

                        closestPillar?.let { onPillarSelected(it) }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = (size.minDimension / 2f) * 0.76f

            // 1. Draw web grid rings (Heptagonal rings)
            val gridLevels = listOf(0.2f, 0.4f, 0.6f, 0.7f, 0.8f, 1.0f)
            for (level in gridLevels) {
                val gridPath = Path()
                for (i in 0 until count) {
                    val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                    val r = maxRadius * level
                    val x = center.x + (r * cos(angle)).toFloat()
                    val y = center.y + (r * sin(angle)).toFloat()
                    if (i == 0) gridPath.moveTo(x, y) else gridPath.lineTo(x, y)
                }
                gridPath.close()

                val isIdealRing = (level == 0.6f)
                val strokeColor = if (isIdealRing) Color(0x5534D399) else Color(0x1A64748B)
                val strokeWidth = if (isIdealRing) 2.dp.toPx() else 1.dp.toPx()

                drawPath(
                    path = gridPath,
                    color = strokeColor,
                    style = Stroke(width = strokeWidth)
                )
            }

            // 2. Draw 7 axes spokes radiating from center
            for (i in 0 until count) {
                val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                val endX = center.x + (maxRadius * cos(angle)).toFloat()
                val endY = center.y + (maxRadius * sin(angle)).toFloat()
                drawLine(
                    color = Color(0x2E475569),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            // 3. Draw Golden Zone shaded ring (40% to 70% target zone)
            val outerGolden = Path()
            val innerGolden = Path()

            for (i in 0 until count) {
                val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                val rOuter = maxRadius * 0.70f
                val x = center.x + (rOuter * cos(angle)).toFloat()
                val y = center.y + (rOuter * sin(angle)).toFloat()
                if (i == 0) outerGolden.moveTo(x, y) else outerGolden.lineTo(x, y)
            }
            outerGolden.close()

            for (i in 0 until count) {
                val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                val rInner = maxRadius * 0.40f
                val x = center.x + (rInner * cos(angle)).toFloat()
                val y = center.y + (rInner * sin(angle)).toFloat()
                if (i == 0) innerGolden.moveTo(x, y) else innerGolden.lineTo(x, y)
            }
            innerGolden.close()

            drawPath(
                path = outerGolden,
                color = Color(0x1F10B981) // soft emerald glow
            )
            drawPath(
                path = innerGolden,
                color = Color(0xFF101722) // cut out inner core
            )

            drawPath(
                path = outerGolden,
                color = Color(0x4010B981),
                style = Stroke(width = 1.dp.toPx())
            )
            drawPath(
                path = innerGolden,
                color = Color(0x4010B981),
                style = Stroke(width = 1.dp.toPx())
            )

            // 4. Draw User Score Polygon
            val userPath = Path()
            val userPoints = mutableListOf<Offset>()

            for (i in 0 until count) {
                val angle = -Math.PI / 2 + (i * 2 * Math.PI / count)
                val targetScore = scores[i]
                val animatedScore = (targetScore * animationProgress.value).coerceIn(5f, 100f)
                val r = maxRadius * (animatedScore / 100f)
                val x = center.x + (r * cos(angle)).toFloat()
                val y = center.y + (r * sin(angle)).toFloat()
                val pt = Offset(x, y)
                userPoints.add(pt)
                if (i == 0) userPath.moveTo(x, y) else userPath.lineTo(x, y)
            }
            userPath.close()

            // Fill user polygon with dynamic gradient
            drawPath(
                path = userPath,
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x7310B981),
                        Color(0x3DF59E0B),
                        Color(0x1A090D14)
                    ),
                    center = center,
                    radius = maxRadius
                )
            )

            // Stroke user polygon with 7-pillar spectrum
            drawPath(
                path = userPath,
                brush = Brush.sweepGradient(
                    colors = listOf(
                        PillarWork,
                        PillarHealth,
                        PillarCreation,
                        PillarFinance,
                        PillarRelations,
                        PillarLeisure,
                        PillarReading,
                        PillarWork
                    ),
                    center = center
                ),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // 5. Draw glowing nodes on each vertex
            userPoints.forEachIndexed { i, pt ->
                val pillar = pillars[i]
                val isSelected = selectedPillar == pillar
                val nodeColor = pillar.color

                if (isSelected) {
                    drawCircle(
                        color = nodeColor.copy(alpha = 0.4f),
                        radius = 12.dp.toPx(),
                        center = pt
                    )
                }

                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = 6.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = nodeColor,
                    radius = 4.5.dp.toPx(),
                    center = pt
                )
            }

            // 6. Digital Noise Fog Overlay if active
            if (hasDigitalNoiseFog) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0x266366F1),
                            Color(0x734338CA),
                            Color(0x8C1E1B4B)
                        ),
                        center = center,
                        radius = maxRadius * 1.15f
                    ),
                    radius = maxRadius * 1.15f,
                    center = center
                )

                for (k in 0 until 18) {
                    val angleNoise = (k * 20) * (Math.PI / 180)
                    val rx = center.x + ((maxRadius * 0.95f) * cos(angleNoise)).toFloat()
                    val ry = center.y + ((maxRadius * 0.95f) * sin(angleNoise)).toFloat()
                    drawCircle(
                        color = Color(0x66A5B4FC),
                        radius = 1.5.dp.toPx(),
                        center = Offset(rx, ry)
                    )
                }
            }
        }

        // Radar Labels overlaid at the 7 cardinal points
        RadarLabelsOverlay(
            pillars = pillars,
            scores = scores,
            selectedPillar = selectedPillar,
            onPillarSelected = onPillarSelected
        )
    }
}

@Composable
private fun BoxScope.RadarLabelsOverlay(
    pillars: List<PillarType>,
    scores: List<Float>,
    selectedPillar: PillarType?,
    onPillarSelected: (PillarType) -> Unit
) {
    // 0: Work (Top)
    RadarPillarBadge(
        pillar = pillars[0],
        score = scores[0].toInt(),
        isSelected = selectedPillar == pillars[0],
        onClick = { onPillarSelected(pillars[0]) },
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 2.dp)
    )

    // 1: Health (Top Right)
    RadarPillarBadge(
        pillar = pillars[1],
        score = scores[1].toInt(),
        isSelected = selectedPillar == pillars[1],
        onClick = { onPillarSelected(pillars[1]) },
        modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(end = 6.dp, top = 46.dp)
    )

    // 2: Creation (Center Right)
    RadarPillarBadge(
        pillar = pillars[2],
        score = scores[2].toInt(),
        isSelected = selectedPillar == pillars[2],
        onClick = { onPillarSelected(pillars[2]) },
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .padding(end = 0.dp)
    )

    // 3: Finance (Bottom Right)
    RadarPillarBadge(
        pillar = pillars[3],
        score = scores[3].toInt(),
        isSelected = selectedPillar == pillars[3],
        onClick = { onPillarSelected(pillars[3]) },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(end = 16.dp, bottom = 4.dp)
    )

    // 4: Relations (Bottom Left)
    RadarPillarBadge(
        pillar = pillars[4],
        score = scores[4].toInt(),
        isSelected = selectedPillar == pillars[4],
        onClick = { onPillarSelected(pillars[4]) },
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 16.dp, bottom = 4.dp)
    )

    // 5: Leisure (Center Left)
    RadarPillarBadge(
        pillar = pillars[5],
        score = scores[5].toInt(),
        isSelected = selectedPillar == pillars[5],
        onClick = { onPillarSelected(pillars[5]) },
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 0.dp)
    )

    // 6: Reading (Top Left)
    RadarPillarBadge(
        pillar = pillars[6],
        score = scores[6].toInt(),
        isSelected = selectedPillar == pillars[6],
        onClick = { onPillarSelected(pillars[6]) },
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = 6.dp, top = 46.dp)
    )
}

@Composable
private fun RadarPillarBadge(
    pillar: PillarType,
    score: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val zone = pillar.getZone(score)
    val shortName = when (pillar) {
        PillarType.WORK -> "Trabalho"
        PillarType.HEALTH -> "Saúde"
        PillarType.CREATION -> "Criação"
        PillarType.FINANCE -> "Finanças"
        PillarType.PURPOSE -> "Propósito"
        PillarType.READING -> "Leitura"
        PillarType.LEISURE -> "Lazer"
        PillarType.RELATIONS -> "Relações"
    }

    androidx.compose.material3.Surface(
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = if (isSelected) DarkSurfaceElevated else DarkSurfaceVariant.copy(alpha = 0.85f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) pillar.color else zone.color.copy(alpha = 0.4f)
        ),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(zone.color, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = shortName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSelected) Color.White else TextPrimary
                ),
                maxLines = 1,
                softWrap = false
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$score",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = zone.color
                ),
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
