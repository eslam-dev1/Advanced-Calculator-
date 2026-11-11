package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.LocalAccentColor
import kotlin.math.roundToInt

/**
 * MiniCalculatorOverlay - Floating on-screen mini calculator overlay.
 * Stays on top of screens, draggable anywhere, allows quick calculations
 * without losing context of what's underneath.
 */
@Composable
fun MiniCalculatorOverlay(
    uiState: CalculatorUiState,
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimalPoint: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onEquals: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val accentColor = LocalAccentColor.current
    val scrollState = rememberScrollState()

    val resultText = when {
        uiState.errorMessage != null -> uiState.errorMessage ?: "Error"
        uiState.finalResult != null -> uiState.finalResult ?: ""
        uiState.previewResult != null -> uiState.previewResult ?: ""
        uiState.expression.isEmpty() -> "0"
        else -> ""
    }

    LaunchedEffect(resultText) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    AnimatedVisibility(
        visible = uiState.isMiniOverlayMode,
        enter = fadeIn() + scaleIn(initialScale = 0.85f),
        exit = fadeOut() + scaleOut(targetScale = 0.85f)
    ) {
        Box(
            modifier = modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .padding(16.dp)
                .testTag("mini_calculator_overlay")
        ) {
            Card(
                modifier = Modifier
                    .width(260.dp)
                    .shadow(16.dp, RoundedCornerShape(20.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            listOf(accentColor.copy(alpha = 0.8f), accentColor.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    // Title & Drag Handle Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    offsetX += dragAmount.x
                                    offsetY += dragAmount.y
                                }
                            }
                            .padding(horizontal = 6.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DragHandle,
                                contentDescription = "Drag to move",
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = androidx.compose.ui.res.stringResource(com.example.R.string.mini_calculator),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = accentColor
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier.size(24.dp).testTag("mini_calc_close")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.mini_calculator_close),
                                    tint = Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mini Display Box (Glowing LCD)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        accentColor.copy(alpha = 0.12f),
                                        Color(0xFF090E18)
                                    )
                                )
                            )
                            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            // Formula preview
                            Text(
                                text = if (uiState.expression.isEmpty()) " " else uiState.expression,
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8),
                                maxLines = 1,
                                textAlign = TextAlign.End
                            )

                            // Result
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val displayText = if (uiState.finalResult != null || uiState.previewResult != null) resultText else if (uiState.expression.isEmpty()) "0" else ""
                                Text(
                                    text = "$displayText|",
                                    color = accentColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.End,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Compact 4x5 Keypad
                    val rowSpacing = 4.dp
                    val colSpacing = 4.dp

                    // Row 1: C, ⌫, %, ÷
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(colSpacing)
                    ) {
                        MiniKey(text = "C", color = com.example.ui.theme.CyberCrimson, bg = com.example.ui.theme.CyberCrimsonBg, weight = 1f, onClick = onClear)
                        MiniKey(icon = Icons.AutoMirrored.Filled.Backspace, color = com.example.ui.theme.CyberPurple, bg = com.example.ui.theme.CyberPurpleBg, weight = 1f, onClick = onBackspace)
                        MiniKey(text = "%", color = com.example.ui.theme.CyberPurple, bg = com.example.ui.theme.CyberPurpleBg, weight = 1f, onClick = { onOperator("%") })
                        MiniKey(text = "÷", color = com.example.ui.theme.CyberAmber, bg = com.example.ui.theme.CyberAmberBg, weight = 1f, onClick = { onOperator("÷") })
                    }

                    Spacer(modifier = Modifier.height(rowSpacing))

                    // Row 2: 7, 8, 9, ×
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(colSpacing)
                    ) {
                        MiniKey(text = "7", weight = 1f, onClick = { onDigit("7") })
                        MiniKey(text = "8", weight = 1f, onClick = { onDigit("8") })
                        MiniKey(text = "9", weight = 1f, onClick = { onDigit("9") })
                        MiniKey(text = "×", color = com.example.ui.theme.CyberAmber, bg = com.example.ui.theme.CyberAmberBg, weight = 1f, onClick = { onOperator("×") })
                    }

                    Spacer(modifier = Modifier.height(rowSpacing))

                    // Row 3: 4, 5, 6, −
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(colSpacing)
                    ) {
                        MiniKey(text = "4", weight = 1f, onClick = { onDigit("4") })
                        MiniKey(text = "5", weight = 1f, onClick = { onDigit("5") })
                        MiniKey(text = "6", weight = 1f, onClick = { onDigit("6") })
                        MiniKey(text = "−", color = com.example.ui.theme.CyberAmber, bg = com.example.ui.theme.CyberAmberBg, weight = 1f, onClick = { onOperator("−") })
                    }

                    Spacer(modifier = Modifier.height(rowSpacing))

                    // Row 4: 1, 2, 3, +
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(colSpacing)
                    ) {
                        MiniKey(text = "1", weight = 1f, onClick = { onDigit("1") })
                        MiniKey(text = "2", weight = 1f, onClick = { onDigit("2") })
                        MiniKey(text = "3", weight = 1f, onClick = { onDigit("3") })
                        MiniKey(text = "+", color = com.example.ui.theme.CyberAmber, bg = com.example.ui.theme.CyberAmberBg, weight = 1f, onClick = { onOperator("+") })
                    }

                    Spacer(modifier = Modifier.height(rowSpacing))

                    // Row 5: 0, ., =
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(colSpacing)
                    ) {
                        MiniKey(text = "0", weight = 2f, onClick = { onDigit("0") })
                        MiniKey(text = ".", weight = 1f, onClick = onDecimalPoint)
                        MiniKey(text = "=", color = Color(0xFF002530), bg = accentColor, weight = 1f, onClick = onEquals)
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.MiniKey(
    text: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    color: Color = Color.White,
    bg: Color = com.example.ui.theme.KeypadNumberBg,
    weight: Float,
    onClick: () -> Unit
) {
    val view = androidx.compose.ui.platform.LocalView.current
    val isSoundEnabled = com.example.ui.theme.LocalSoundEnabled.current

    Box(
        modifier = Modifier
            .weight(weight)
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectDragGestures(onDrag = { _, _ -> }) // Prevent event bubbling to parent drag
            }
            .clickable {
                view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                if (isSoundEnabled) {
                    view.playSoundEffect(android.view.SoundEffectConstants.CLICK)
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = color,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
