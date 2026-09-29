package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AvatarEmotion
import com.example.ui.theme.DarkSurfaceElevated

/**
 * 16-Bit Modern RPG Dialogue Portrait Component (Stardew Valley / Celeste style)
 * High-definition 32x32 pixel art with anatomical face contours, rich shading,
 * and emotive facial expressions for Happy, Tired, Sad, and Exhausted states.
 */
@Composable
fun PixelAvatarView(
    emotion: AvatarEmotion,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    showSpeechBubble: Boolean = false,
    speechText: String = emotion.defaultSpeech,
    onClick: () -> Unit = {}
) {
    // Idle gentle breathing animation
    val infiniteTransition = rememberInfiniteTransition(label = "rpg_avatar_anim")
    val idleOffsetY by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "idle_offset"
    )

    // Periodic natural eye blink
    val blinkProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 3800
                0.0f at 0
                0.0f at 3400
                1.0f at 3550
                0.0f at 3700
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "blink_cycle"
    )

    // Aura pulse
    val auraRadiusScale by infiniteTransition.animateFloat(
        initialValue = 0.44f,
        targetValue = 0.52f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (showSpeechBubble) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = BorderStroke(1.dp, emotion.primaryColor.copy(alpha = 0.6f)),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .widthIn(max = 240.dp)
            ) {
                Text(
                    text = speechText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = emotion.primaryColor,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    drawCircle(
                        color = emotion.auraColor,
                        radius = size.toPx() * auraRadiusScale,
                        center = center
                    )
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
                .testTag("pixel_avatar_box"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = idleOffsetY.dp)
            ) {
                draw16BitRpgPortrait(
                    emotion = emotion,
                    isBlinking = blinkProgress > 0.4f
                )
            }
        }
    }
}

/**
 * 32x32 Authentic 16-Bit RPG Dialogue Portrait Renderer
 */
private fun DrawScope.draw16BitRpgPortrait(
    emotion: AvatarEmotion,
    isBlinking: Boolean
) {
    val grid = 32
    val pw = size.width / grid
    val ph = size.height / grid

    fun px(x: Int, y: Int, color: Color) {
        if (x in 0 until grid && y in 0 until grid) {
            drawRect(
                color = color,
                topLeft = Offset(x * pw, y * ph),
                size = Size(pw + 0.5f, ph + 0.5f)
            )
        }
    }

    fun fillRect(x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
        for (y in y1..y2) {
            for (x in x1..x2) {
                px(x, y, color)
            }
        }
    }

    // 16-Bit RPG Color Palette
    val cFrameOuter = Color(0xFF1E232A)
    val cFrameGold = Color(0xFFC99738)
    val cFrameGoldLight = Color(0xFFFFD56B)
    val cFrameBg = Color(0xFF0F141C)

    // Skin tones
    val isExhausted = emotion == AvatarEmotion.EXHAUSTED
    val cSkin = if (isExhausted) Color(0xFFE8D5CE) else Color(0xFFFFDBB5)
    val cSkinShadow = if (isExhausted) Color(0xFFCBB2A9) else Color(0xFFE6B88E)
    val cSkinHighlight = if (isExhausted) Color(0xFFF3E7E2) else Color(0xFFFFE8D1)

    // Hair tones (Warm chestnut brown with golden hazel highlights)
    val cHairDark = Color(0xFF26150E)
    val cHairBase = Color(0xFF4E2C1D)
    val cHairLight = Color(0xFF7A4A32)
    val cHairGlint = Color(0xFFA66946)

    // Clothes
    val cCloak = Color(0xFF232C3D)
    val cCloakShadow = Color(0xFF181F2C)
    val cCollar = Color(0xFF3B4860)

    // Accents & Emotions
    val cBlush = Color(0xFFFF7285)
    val cEyeWhite = Color(0xFFF5F7FA)
    val cPupil = Color(0xFF11141A)
    val cIris = emotion.primaryColor
    val cSweatDrop = Color(0xFF38BDF8)
    val cTear = Color(0xFF38BDF8)
    val cFatigueRing = Color(0xFF7C6F9C)
    val cMouthInterior = Color(0xFF993B47)

    // ==========================================
    // 1. RPG PORTRAIT BOX FRAME (Classic 32x32 window)
    // ==========================================
    // Outer border
    fillRect(1, 1, 30, 30, cFrameBg)
    for (i in 1..30) {
        px(i, 1, cFrameGold); px(i, 30, cFrameGold)
        px(1, i, cFrameGold); px(30, i, cFrameGold)
    }
    // Highlight inner bevel
    for (i in 2..29) {
        px(i, 2, cFrameGoldLight)
        px(2, i, cFrameGoldLight)
    }
    // Corner rivets
    px(2, 2, Color.White); px(29, 2, Color.White)
    px(2, 29, Color.White); px(29, 29, Color.White)

    // ==========================================
    // 2. CHARACTER BODY / CLOAK (Rows 24 to 29)
    // ==========================================
    fillRect(8, 25, 23, 29, cCloak)
    fillRect(6, 26, 7, 29, cCloakShadow)
    fillRect(24, 26, 25, 29, cCloakShadow)

    // Collar / Tunic Trim
    fillRect(11, 23, 20, 25, cCollar)
    fillRect(14, 24, 17, 26, cSkinShadow) // Neck hollow
    px(15, 24, cSkin)
    px(16, 24, cSkin)

    // Brooch/Badge with Emotion Color
    fillRect(14, 26, 17, 27, emotion.primaryColor)
    px(15, 26, Color.White)

    // ==========================================
    // 3. HEAD & FACE STRUCTURE (Rows 8 to 22)
    // ==========================================
    // Base Face Fill
    for (y in 9..20) {
        val inset = when (y) {
            9 -> 8
            10 -> 7
            11, 12, 13, 14, 15, 16 -> 6
            17 -> 7
            18 -> 8
            19 -> 10
            20 -> 12
            else -> 13
        }
        val rightEdge = 31 - inset
        fillRect(inset, y, rightEdge, y, cSkin)
    }

    // Jawline & Chin Shading (Anatomical taper)
    px(13, 21, cSkin); px(14, 21, cSkin); px(17, 21, cSkin); px(18, 21, cSkin)
    fillRect(14, 22, 17, 22, cSkinShadow) // Chin tip shadow
    fillRect(10, 19, 11, 20, cSkinShadow) // Left jaw shade
    fillRect(20, 19, 21, 20, cSkinShadow) // Right jaw shade

    // Ears
    fillRect(5, 13, 5, 16, cSkin)
    px(5, 14, cSkinShadow)
    fillRect(26, 13, 26, 16, cSkin)
    px(26, 14, cSkinShadow)

    // Cheek highlights
    px(9, 13, cSkinHighlight); px(10, 13, cSkinHighlight)
    px(21, 13, cSkinHighlight); px(22, 13, cSkinHighlight)

    // ==========================================
    // 4. HAIR RENDERING (Voluminous RPG layers)
    // ==========================================
    // Back hair shadow behind ears
    fillRect(4, 10, 5, 20, cHairDark)
    fillRect(26, 10, 27, 20, cHairDark)

    // Main Hair Dome (Rows 4 to 10)
    for (y in 4..8) {
        val startX = when (y) {
            4 -> 10; 5 -> 7; 6 -> 6; 7 -> 5; else -> 5
        }
        val endX = 31 - startX
        fillRect(startX, y, endX, y, cHairBase)
    }

    // Hair Top Highlights & Bangs
    fillRect(10, 5, 21, 6, cHairLight)
    fillRect(12, 5, 17, 5, cHairGlint)

    // Left bangs falling down
    fillRect(6, 8, 8, 11, cHairBase)
    fillRect(7, 9, 8, 12, cHairLight)
    px(8, 13, cHairBase)

    // Center stylish anime bangs
    fillRect(13, 8, 15, 11, cHairBase)
    px(14, 12, cHairLight)
    px(15, 12, cHairBase)

    // Right bangs
    fillRect(23, 8, 25, 11, cHairBase)
    fillRect(23, 9, 24, 12, cHairLight)
    px(23, 13, cHairBase)

    // Hair outline depth
    for (x in 9..22) px(x, 4, cHairDark)
    px(6, 6, cHairDark); px(25, 6, cHairDark)
    px(5, 7, cHairDark); px(26, 7, cHairDark)

    // ==========================================
    // 5. NOSE
    // ==========================================
    px(16, 16, cSkinShadow)
    px(15, 16, cSkinHighlight)

    // ==========================================
    // 6. EYES & BROWS BY EMOTION
    // ==========================================
    if (isBlinking && emotion != AvatarEmotion.EXHAUSTED) {
        // Natural resting closed eyes (eyelashes)
        fillRect(9, 14, 12, 14, cHairDark)
        fillRect(19, 14, 22, 14, cHairDark)
        px(8, 14, cSkinShadow); px(23, 14, cSkinShadow)
    } else {
        when (emotion) {
            AvatarEmotion.HAPPY -> {
                // Cheerful raised arched eyebrows
                fillRect(9, 11, 12, 11, cHairDark)
                px(8, 12, cHairDark)
                fillRect(19, 11, 22, 11, cHairDark)
                px(23, 12, cHairDark)

                // Left Eye (Big, bright anime RPG eye with glints)
                fillRect(9, 13, 12, 15, cEyeWhite)
                fillRect(10, 13, 12, 15, cIris)
                fillRect(11, 14, 12, 15, cPupil)
                px(10, 13, Color.White) // Primary catchlight
                px(12, 15, Color.White) // Secondary glint

                // Right Eye
                fillRect(19, 13, 22, 15, cEyeWhite)
                fillRect(19, 13, 21, 15, cIris)
                fillRect(19, 14, 20, 15, cPupil)
                px(20, 13, Color.White)
                px(22, 15, Color.White)

                // Eyeliner frame
                for (x in 9..12) px(x, 12, cHairDark)
                for (x in 19..22) px(x, 12, cHairDark)

                // Rosy Blushes
                fillRect(8, 16, 11, 16, cBlush)
                fillRect(20, 16, 23, 16, cBlush)
                px(9, 16, Color(0xFFFFB3BA))
                px(21, 16, Color(0xFFFFB3BA))

                // Floating sparkle particles in frame corners
                px(4, 5, Color.Yellow); px(5, 5, Color.White); px(4, 6, Color.Yellow)
                px(27, 6, Color.Yellow); px(26, 6, Color.White); px(27, 7, Color.Yellow)
            }

            AvatarEmotion.TIRED -> {
                // Weary flat angled eyebrows
                fillRect(9, 12, 12, 12, cHairDark)
                fillRect(19, 12, 22, 12, cHairDark)

                // Heavy half-closed eyelids
                fillRect(9, 13, 12, 13, cSkinShadow)
                fillRect(19, 13, 22, 13, cSkinShadow)

                // Left Eye (narrowed, tired)
                fillRect(9, 14, 12, 15, cEyeWhite)
                fillRect(10, 14, 11, 15, cIris)
                px(10, 14, cPupil)

                // Right Eye
                fillRect(19, 14, 22, 15, cEyeWhite)
                fillRect(20, 14, 21, 15, cIris)
                px(20, 14, cPupil)

                // Sweat drop on upper temple brow
                fillRect(24, 10, 25, 11, cSweatDrop)
                px(24, 10, Color.White)
                px(25, 12, cSweatDrop)
            }

            AvatarEmotion.SAD -> {
                // Downturned sad puppy eyebrows
                px(9, 11, cHairDark); fillRect(10, 12, 12, 12, cHairDark)
                px(22, 11, cHairDark); fillRect(19, 12, 21, 12, cHairDark)

                // Left Eye (glistening, looking down)
                fillRect(9, 13, 12, 15, cEyeWhite)
                fillRect(10, 14, 12, 15, cIris)
                px(11, 14, cPupil)
                px(10, 14, Color.White)

                // Right Eye
                fillRect(19, 13, 22, 15, cEyeWhite)
                fillRect(19, 14, 21, 15, cIris)
                px(20, 14, cPupil)
                px(21, 14, Color.White)

                // Tear running down cheek
                fillRect(21, 16, 21, 17, cTear)
                px(21, 16, Color.White)
                fillRect(21, 18, 22, 18, cTear)
            }

            AvatarEmotion.EXHAUSTED -> {
                // Distressed diagonal eyebrows
                px(8, 12, cHairDark); fillRect(9, 11, 12, 11, cHairDark)
                px(23, 12, cHairDark); fillRect(19, 11, 22, 11, cHairDark)

                // Heavy Fatigue dark eye rings
                fillRect(8, 16, 13, 16, cFatigueRing)
                fillRect(18, 16, 23, 16, cFatigueRing)

                // Dizzy spiral / 'X' exhaustion eyes
                fillRect(9, 13, 12, 15, Color(0xFFEDE9FE))
                fillRect(19, 13, 22, 15, Color(0xFFEDE9FE))

                // Left Eye 'X'
                px(9, 13, cHairDark); px(12, 13, cHairDark)
                px(10, 14, Color.Red); px(11, 14, Color.Red)
                px(9, 15, cHairDark); px(12, 15, cHairDark)

                // Right Eye 'X'
                px(19, 13, cHairDark); px(22, 13, cHairDark)
                px(20, 14, Color.Red); px(21, 14, Color.Red)
                px(19, 15, cHairDark); px(22, 15, cHairDark)

                // Stress heat steam rising from temples
                px(4, 7, Color(0x99EF4444)); px(5, 6, Color(0x99EF4444))
                px(27, 7, Color(0x99EF4444)); px(26, 6, Color(0x99EF4444))
            }
        }
    }

    // ==========================================
    // 7. MOUTH RENDERING BY EMOTION
    // ==========================================
    when (emotion) {
        AvatarEmotion.HAPPY -> {
            // Broad confident smile
            fillRect(14, 18, 17, 19, cMouthInterior)
            fillRect(14, 18, 17, 18, Color.White) // Teeth glint
            px(13, 17, cHairDark); px(18, 17, cHairDark) // Smile corners
            fillRect(14, 20, 17, 20, cSkinShadow) // Lower lip shade
        }
        AvatarEmotion.TIRED -> {
            // Flat sighed mouth
            fillRect(14, 18, 17, 18, cHairDark)
            px(14, 19, cSkinShadow); px(17, 19, cSkinShadow)
            // Tiny sigh breath particle
            px(19, 19, Color(0x88CBD5E1))
            px(20, 19, Color(0x66CBD5E1))
        }
        AvatarEmotion.SAD -> {
            // Downturned trembling sad mouth
            fillRect(14, 18, 17, 18, cHairDark)
            px(13, 19, cHairDark); px(18, 19, cHairDark)
            fillRect(14, 19, 17, 19, cSkinShadow)
        }
        AvatarEmotion.EXHAUSTED -> {
            // Gasping open mouth with tongue hanging
            fillRect(14, 18, 17, 20, cMouthInterior)
            fillRect(15, 19, 17, 21, Color(0xFFFF5252)) // Tongue
            px(13, 18, cHairDark); px(18, 18, cHairDark)
        }
    }
}
