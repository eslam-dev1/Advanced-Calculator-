package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.math.ExpressionEvaluator
import com.example.ui.CalculatorUiState
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.DisplayResultColor
import com.example.ui.theme.LocalAccentColor
import com.example.ui.theme.ObsidianDark
import kotlin.math.abs

/**
 * GrapherScreen - Optimized for performance.
 * Heavy mathematical path calculations are cached to avoid frame drops during draw calls.
 */
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle

@Composable
fun GrapherScreen(
    uiState: CalculatorUiState,
    onUpdateFunction: (String) -> Unit,
    onZoom: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    val presets = remember { listOf("sin(x)", "cos(x)", "x^2", "x^3 - 4*x", "sqrt(x)", "1/x", "abs(x)") }
    var touchX by remember { androidx.compose.runtime.mutableStateOf<Float?>(null) }
    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Function Input & Presets Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.graph_function),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DisplayResultColor
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { onZoom(true) },
                            modifier = Modifier.size(36.dp).testTag("btn_zoom_in")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.zoom_in), tint = accentColor)
                        }
                        IconButton(
                            onClick = { onZoom(false) },
                            modifier = Modifier.size(36.dp).testTag("btn_zoom_out")
                        ) {
                            Icon(imageVector = Icons.Default.Remove, contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.zoom_out), tint = accentColor)
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.graphFunction,
                    onValueChange = onUpdateFunction,
                    modifier = Modifier.fillMaxWidth().testTag("graph_function_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = DarkBorder,
                        focusedTextColor = DisplayResultColor,
                        unfocusedTextColor = DisplayResultColor,
                        cursorColor = accentColor
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Presets
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets, key = { it }) { preset ->
                        val isSelected = uiState.graphFunction == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) accentColor.copy(alpha = 0.25f) else DarkSurfaceElevated)
                                .border(1.dp, if (isSelected) accentColor else DarkBorder, RoundedCornerShape(8.dp))
                                .clickable { onUpdateFunction(preset) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("preset_$preset"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = preset,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) accentColor else DisplayFormulaColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Canvas Graph View - Interactive 2D Coordinate Plane
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, DarkBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                val canvasWidth = constraints.maxWidth.toFloat()
                val canvasHeight = constraints.maxHeight.toFloat()
                
                val rangeX = uiState.graphRangeX
                val expr = uiState.graphFunction
                val isPowerSaving = com.example.ui.theme.LocalPowerSavingMode.current

                // Pre-calculate path for high performance rendering
                val graphPath by remember(expr, rangeX, canvasWidth, canvasHeight, isPowerSaving) {
                    derivedStateOf {
                        val path = Path()
                        if (canvasWidth <= 0 || canvasHeight <= 0) return@derivedStateOf path
                        
                        val centerX = canvasWidth / 2f
                        val centerY = canvasHeight / 2f
                        val scaleX = canvasWidth / (2f * rangeX)
                        val rangeY = rangeX * (canvasHeight / canvasWidth)
                        val scaleY = canvasHeight / (2f * rangeY)

                        var isFirstPoint = true
                        val stepCount = if (isPowerSaving) 60f else 200f
                        val step = canvasWidth / stepCount

                        var px = 0f
                        while (px <= canvasWidth) {
                            val mathX = (px - centerX) / scaleX
                            val substituted = substituteX(expr, mathX.toDouble())
                            val eval = ExpressionEvaluator.evaluate(substituted, isDegreeMode = false)

                            if (eval is ExpressionEvaluator.EvalResult.Success) {
                                val mathY = eval.value
                                if (!mathY.isNaN() && !mathY.isInfinite() && abs(mathY) <= rangeY * 3) {
                                    val py = centerY - (mathY.toFloat() * scaleY)
                                    if (py in -canvasHeight..canvasHeight * 2) {
                                        if (isFirstPoint) {
                                            path.moveTo(px, py)
                                            isFirstPoint = false
                                        } else {
                                            path.lineTo(px, py)
                                        }
                                    } else {
                                        isFirstPoint = true
                                    }
                                } else {
                                    isFirstPoint = true
                                }
                            } else {
                                isFirstPoint = true
                            }
                            px += step
                        }
                        path
                    }
                }

                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = { offset ->
                                    touchX = offset.x
                                },
                                onTap = { offset ->
                                    touchX = offset.x
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                if (zoom > 1.05f) onZoom(true)
                                else if (zoom < 0.95f) onZoom(false)
                            }
                        }
                        .testTag("graph_canvas")
                ) {
                    val width = size.width
                    val height = size.height
                    val centerX = width / 2f
                    val centerY = height / 2f

                    val scaleX = width / (2f * rangeX)
                    val rangeY = rangeX * (height / width)
                    val scaleY = height / (2f * rangeY)

                    // Grid Lines
                    val gridColor = Color.White.copy(alpha = 0.08f)
                    val axisColor = Color.White.copy(alpha = 0.35f)

                    val stepX = if (rangeX > 20) 10 else if (rangeX > 10) 5 else 2
                    for (x in -rangeX.toInt()..rangeX.toInt() step stepX) {
                        if (x == 0) continue
                        val px = centerX + x * scaleX
                        drawLine(gridColor, Offset(px, 0f), Offset(px, height), strokeWidth = 1.dp.toPx())

                        // Axis Tick Label
                        drawText(
                            textMeasurer = textMeasurer,
                            text = x.toString(),
                            style = TextStyle(color = Color.White.copy(alpha = 0.4f), fontSize = 9.sp),
                            topLeft = Offset(px - 6.dp.toPx(), centerY + 4.dp.toPx())
                        )
                    }

                    for (y in -rangeY.toInt()..rangeY.toInt() step stepX) {
                        if (y == 0) continue
                        val py = centerY - y * scaleY
                        drawLine(gridColor, Offset(0f, py), Offset(width, py), strokeWidth = 1.dp.toPx())
                    }

                    // Main X & Y Axes
                    drawLine(axisColor, Offset(0f, centerY), Offset(width, centerY), strokeWidth = 1.5.dp.toPx())
                    drawLine(axisColor, Offset(centerX, 0f), Offset(centerX, height), strokeWidth = 1.5.dp.toPx())

                    // Glow effect backdrop
                    drawPath(
                        path = graphPath,
                        color = accentColor.copy(alpha = 0.3f),
                        style = Stroke(width = 5.dp.toPx())
                    )

                    // Main Function Curve Path
                    drawPath(
                        path = graphPath,
                        color = accentColor,
                        style = Stroke(width = 2.5.dp.toPx())
                    )

                    // Touch Inspection Crosshair & Tooltip
                    touchX?.let { tx ->
                        val mathX = (tx - centerX) / scaleX
                        val substituted = substituteX(expr, mathX.toDouble())
                        val eval = ExpressionEvaluator.evaluate(substituted, isDegreeMode = false)

                        if (eval is ExpressionEvaluator.EvalResult.Success) {
                            val mathY = eval.value
                            if (!mathY.isNaN() && !mathY.isInfinite()) {
                                val py = centerY - (mathY.toFloat() * scaleY)

                                // Crosshair dashed lines
                                drawLine(
                                    color = accentColor.copy(alpha = 0.5f),
                                    start = Offset(tx, 0f),
                                    end = Offset(tx, height),
                                    strokeWidth = 1.dp.toPx()
                                )
                                drawLine(
                                    color = accentColor.copy(alpha = 0.5f),
                                    start = Offset(0f, py),
                                    end = Offset(width, py),
                                    strokeWidth = 1.dp.toPx()
                                )

                                // Target Node Circle
                                drawCircle(
                                    color = accentColor,
                                    radius = 6.dp.toPx(),
                                    center = Offset(tx, py)
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 3.dp.toPx(),
                                    center = Offset(tx, py)
                                )
                            }
                        }
                    }
                }

                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.graph_range_x, -rangeX.toInt(), rangeX.toInt()),
                    fontSize = 11.sp,
                    color = DisplayFormulaColor,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }
        }
    }
}

private fun substituteX(expr: String, xVal: Double): String {
    val formattedX = if (xVal >= 0) xVal.toString() else "(0$xVal)"
    return expr.replace(Regex("(?<![a-zA-Z])x(?![a-zA-Z])"), formattedX)
}
