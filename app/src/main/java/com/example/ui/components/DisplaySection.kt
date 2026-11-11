package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState
import com.example.ui.theme.ErrorColor
import com.example.ui.theme.LocalAccentColor

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

    // Glowing vibrant LCD screen gradient strictly matching the reference preview
    val lcdBackgroundBrush = Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.18f),
            Color(0xFF0C1422),
            Color(0xFF080D18)
        )
    )
    val lcdBorderBrush = Brush.linearGradient(
        colors = listOf(
            accentColor,
            Color(0xFF00B0FF).copy(alpha = 0.7f),
            accentColor.copy(alpha = 0.4f),
            Color(0xFF00E5FF).copy(alpha = 0.85f)
        )
    )

    // Cursor blink animation
    val infiniteTransition = rememberInfiniteTransition(label = "cursor_blink")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 650),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursor_alpha"
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
        // Subtle Cyber Matrix Grid Background & Corner Crosshairs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Grid lines
            val gridColor = accentColor.copy(alpha = 0.04f)
            val step = 28.dp.toPx()
            var x = step
            while (x < w) {
                drawLine(gridColor, Offset(x, 0f), Offset(x, h), strokeWidth = 1f)
                x += step
            }
            var y = step
            while (y < h) {
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 1f)
                y += step
            }

            // HUD Crosshairs at the corners (┌ ┐ └ ┘)
            val markColor = accentColor.copy(alpha = 0.45f)
            val markLen = 14.dp.toPx()
            val mStroke = 1.5.dp.toPx()

            // Top-left
            drawLine(markColor, Offset(2f, 2f), Offset(2f + markLen, 2f), strokeWidth = mStroke, cap = StrokeCap.Round)
            drawLine(markColor, Offset(2f, 2f), Offset(2f, 2f + markLen), strokeWidth = mStroke, cap = StrokeCap.Round)

            // Top-right
            drawLine(markColor, Offset(w - 2f, 2f), Offset(w - 2f - markLen, 2f), strokeWidth = mStroke, cap = StrokeCap.Round)
            drawLine(markColor, Offset(w - 2f, 2f), Offset(w - 2f, 2f + markLen), strokeWidth = mStroke, cap = StrokeCap.Round)

            // Bottom-left
            drawLine(markColor, Offset(2f, h - 2f), Offset(2f + markLen, h - 2f), strokeWidth = mStroke, cap = StrokeCap.Round)
            drawLine(markColor, Offset(2f, h - 2f), Offset(2f, h - 2f - markLen), strokeWidth = mStroke, cap = StrokeCap.Round)

            // Bottom-right
            drawLine(markColor, Offset(w - 2f, h - 2f), Offset(w - 2f - markLen, h - 2f), strokeWidth = mStroke, cap = StrokeCap.Round)
            drawLine(markColor, Offset(w - 2f, h - 2f), Offset(w - 2f, h - 2f - markLen), strokeWidth = mStroke, cap = StrokeCap.Round)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Status Header Row (DEG/RAD segmented switch, status indicators & action icons)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AngleModeSwitch(
                        isDegreeMode = uiState.isDegreeMode,
                        onToggle = { onToggleDegreeMode() }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Audio visualizer bars beside the pill (as shown in reference mockup)
                    Box(modifier = Modifier.padding(start = 2.dp)) {
                        Canvas(modifier = Modifier.size(width = 18.dp, height = 14.dp)) {
                            val barColor = accentColor.copy(alpha = 0.6f)
                            drawLine(barColor, Offset(3f, 10f), Offset(3f, 4f), strokeWidth = 2f, cap = StrokeCap.Round)
                            drawLine(barColor, Offset(8f, 12f), Offset(8f, 2f), strokeWidth = 2f, cap = StrokeCap.Round)
                            drawLine(barColor, Offset(13f, 9f), Offset(13f, 5f), strokeWidth = 2f, cap = StrokeCap.Round)
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleVoice,
                        modifier = Modifier.size(34.dp).testTag("btn_voice_input")
                    ) {
                        Icon(
                            imageVector = if (uiState.isVoiceListening) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Voice Input",
                            tint = if (uiState.isVoiceListening) accentColor else Color(0xFF64748B),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    IconButton(
                        onClick = onOpenHistory,
                        modifier = Modifier.size(34.dp).testTag("btn_display_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "History",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            // Calculation History Stack (Faint stacked history calculations on top-right)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Show recent calculation history item if available
                val recentHistory = uiState.historyList.firstOrNull()
                if (recentHistory != null && uiState.finalResult == null) {
                    Text(
                        text = "${recentHistory.expression} =",
                        color = Color(0xFF64748B).copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                    Text(
                        text = "${recentHistory.result} +",
                        color = Color(0xFF00E5FF).copy(alpha = 0.65f),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        maxLines = 1
                    )
                }

                // Active expression / formula row (Visual mathematical equation)
                val activeFormula = when {
                    uiState.finalResult != null -> "${uiState.expression} ="
                    uiState.expression.contains(Regex("[+×÷−^%]")) -> uiState.expression
                    else -> ""
                }

                if (activeFormula.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scrollState),
                        horizontalArrangement = Arrangement.End
                    ) {
                        VisualEquationRenderer(
                            expression = activeFormula,
                            color = com.example.ui.theme.CyberAmber,
                            fontSize = 19.sp,
                            modifier = Modifier.testTag("expression_display")
                        )
                    }
                }
            }

            // Main Active Calculation Result (HUGE, glowing, razor-sharp electric cyan digital text)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .horizontalScroll(resultScrollState),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Main active number to display prominently:
                val mainDigits = when {
                    uiState.errorMessage != null -> uiState.errorMessage ?: "Error"
                    uiState.finalResult != null -> uiState.finalResult ?: ""
                    uiState.previewResult != null -> uiState.previewResult ?: ""
                    uiState.expression.isNotEmpty() -> uiState.expression
                    else -> "0"
                }

                val mainColor = when {
                    uiState.errorMessage != null -> ErrorColor
                    else -> accentColor
                }

                val len = mainDigits.length
                val (fontSize, lineHeight) = when {
                    len > 20 -> 22.sp to 28.sp
                    len > 15 -> 28.sp to 34.sp
                    len > 10 -> 36.sp to 42.sp
                    len > 6 -> 44.sp to 50.sp
                    else -> 52.sp to 58.sp
                }

                Box(contentAlignment = Alignment.CenterEnd) {
                    // Soft outer glow shadow pass
                    Text(
                        text = mainDigits,
                        color = mainColor.copy(alpha = 0.28f),
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        softWrap = false,
                        modifier = Modifier.padding(1.dp)
                    )

                    // Sharp core digital digits
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = mainDigits,
                            color = mainColor,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.End,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.testTag("result_display")
                        )

                        // Glowing electric cyan cursor
                        Box(
                            modifier = Modifier
                                .padding(start = 3.dp, bottom = 4.dp)
                                .size(width = 3.5.dp, height = (fontSize.value * 0.72f).dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(mainColor.copy(alpha = cursorAlpha))
                        )
                    }
                }
            }
        }
    }
}
