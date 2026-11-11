package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.DisplayPreviewColor
import com.example.ui.theme.DisplayResultColor
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.LocalAccentColor
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun DisplaySection(
    uiState: CalculatorUiState,
    onToggleDegreeMode: () -> Unit,
    onToggleScientific: () -> Unit,
    onOpenHistory: () -> Unit,
    onToggleVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val accentColor = LocalAccentColor.current
    val surfaceColor = com.example.ui.theme.DarkSurface.copy(alpha = 0.85f)
    val borderColor = accentColor.copy(alpha = 0.35f)

    // Auto-scroll to end whenever expression changes
    LaunchedEffect(uiState.expression) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(surfaceColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(24.dp))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom
        ) {
            // Status Header Row (Angle mode badge, Voice & History actions)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AngleModeSwitch(
                    isDegreeMode = uiState.isDegreeMode,
                    onToggle = { onToggleDegreeMode() }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleVoice,
                        modifier = Modifier.testTag("btn_voice_input")
                    ) {
                        Icon(
                            imageVector = if (uiState.isVoiceListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Voice Input",
                            tint = if (uiState.isVoiceListening) accentColor else com.example.ui.theme.DisplayFormulaColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.testTag("btn_display_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = com.example.ui.theme.DisplayFormulaColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Formula / Expression Display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.End
            ) {
                val exprText = if (uiState.expression.isEmpty()) "" else uiState.expression
                VisualEquationRenderer(
                    expression = exprText,
                    color = com.example.ui.theme.CyberAmber.copy(alpha = 0.9f),
                    fontSize = 22.sp,
                    modifier = Modifier.testTag("expression_display")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Result / Live Preview Display with dynamic accent text and blinking cursor
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val resultText = when {
                    uiState.errorMessage != null -> uiState.errorMessage ?: "Error"
                    uiState.finalResult != null -> uiState.finalResult ?: ""
                    uiState.previewResult != null -> uiState.previewResult ?: ""
                    uiState.expression.isEmpty() -> "0"
                    else -> ""
                }

                val resultColor = when {
                    uiState.errorMessage != null -> ErrorColor
                    uiState.finalResult != null -> accentColor
                    else -> accentColor.copy(alpha = 0.85f)
                }

                val len = resultText.length
                val (fontSize, lineHeight) = when {
                    len > 20 -> 18.sp to 24.sp
                    len > 15 -> 22.sp to 28.sp
                    len > 12 -> 28.sp to 34.sp
                    len > 9 -> 34.sp to 40.sp
                    len > 6 -> 42.sp to 48.sp
                    else -> 52.sp to 58.sp
                }

                val displayText = if (uiState.finalResult != null || uiState.previewResult != null) resultText else if (uiState.expression.isEmpty()) "0" else ""

                Text(
                    text = "$displayText|",
                    color = resultColor,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("result_display")
                )
            }
        }
    }
}

