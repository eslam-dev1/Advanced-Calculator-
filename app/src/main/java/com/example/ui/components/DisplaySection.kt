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
import androidx.compose.ui.graphics.Brush

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
    val resultScrollState = rememberScrollState()
    val accentColor = LocalAccentColor.current

    // Glowing vibrant LCD screen gradient matching the reference design
    val lcdBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.15f),
            Color(0xFF0D1424),
            Color(0xFF090E18)
        )
    )
    val lcdBorderBrush = Brush.linearGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.85f),
            accentColor.copy(alpha = 0.25f),
            Color(0xFF00E5FF).copy(alpha = 0.6f)
        )
    )

    // Auto-scroll result to the end so user always sees the latest entered digits
    LaunchedEffect(uiState.expression, uiState.finalResult, uiState.previewResult) {
        scrollState.animateScrollTo(scrollState.maxValue)
        resultScrollState.animateScrollTo(resultScrollState.maxValue)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(lcdBackgroundBrush)
            .border(2.dp, lcdBorderBrush, RoundedCornerShape(26.dp))
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
                            tint = if (uiState.isVoiceListening) accentColor else Color(0xFF64748B),
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
                            tint = Color(0xFF64748B),
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
                    color = com.example.ui.theme.CyberAmber.copy(alpha = 0.95f),
                    fontSize = 20.sp,
                    modifier = Modifier.testTag("expression_display")
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Result / Live Preview Display with full visibility (horizontal scroll + adaptive sizing)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .horizontalScroll(resultScrollState),
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
                    else -> accentColor.copy(alpha = 0.9f)
                }

                val len = resultText.length
                val (fontSize, lineHeight) = when {
                    len > 22 -> 20.sp to 26.sp
                    len > 16 -> 24.sp to 30.sp
                    len > 12 -> 30.sp to 36.sp
                    len > 8 -> 38.sp to 44.sp
                    len > 5 -> 44.sp to 50.sp
                    else -> 50.sp to 56.sp
                }

                val displayText = if (uiState.finalResult != null || uiState.previewResult != null) resultText else if (uiState.expression.isEmpty()) "0" else ""

                Text(
                    text = "$displayText|",
                    color = resultColor,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.End,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.testTag("result_display")
                )
            }
        }
    }
}

