package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.LocalAccentColor
import kotlin.math.PI
import kotlin.math.sin

/**
 * An animated cyber oscilloscope sine-wave visualizer matching the preview reference design.
 * Rendered between the LCD screen and keypad with glowing neon cyan curves.
 */
@Composable
fun OscilloscopeVisualizer(
    modifier: Modifier = Modifier
) {
    val accentColor = LocalAccentColor.current
    val infiniteTransition = rememberInfiniteTransition(label = "oscilloscope_transition")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.width(130.dp).height(30.dp)) {
            val width = size.width
            val height = size.height
            val centerY = height / 2f
            val amplitude = height * 0.38f
            val waveLength = width * 0.45f

            val path = Path()
            val points = 60
            for (i in 0..points) {
                val x = (i.toFloat() / points) * width
                val angle = (x / waveLength) * 2f * PI.toFloat() + phase
                val y = centerY + sin(angle) * amplitude
                if (i == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
            }

            // Glow pass (soft outer glow)
            drawPath(
                path = path,
                color = accentColor.copy(alpha = 0.35f),
                style = Stroke(width = 4.5.dp.toPx(), cap = StrokeCap.Round)
            )

            // Sharp core wave line
            drawPath(
                path = path,
                color = accentColor,
                style = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
