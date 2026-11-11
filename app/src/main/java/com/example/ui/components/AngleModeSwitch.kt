package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocalAccentColor

/**
 * Animated Segmented Switch to toggle between Degrees (DEG) and Radians (RAD)
 * for high-precision scientific trigonometry calculations.
 */
@Composable
fun AngleModeSwitch(
    isDegreeMode: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val accentColor = LocalAccentColor.current
    val shape = RoundedCornerShape(18.dp)
    val surfaceElevated = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    BoxWithConstraints(
        modifier = modifier
            .width(130.dp)
            .height(34.dp)
            .clip(shape)
            .background(surfaceElevated)
            .border(1.dp, borderColor, shape)
            .padding(2.dp)
            .testTag("angle_mode_switch")
    ) {
        val segmentWidth = maxWidth / 2

        // Animated horizontal offset for sliding indicator
        val indicatorOffsetFraction by animateFloatAsState(
            targetValue = if (isDegreeMode) 0f else 1f,
            animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
            label = "indicator_offset"
        )

        // Sliding Active Indicator
        Box(
            modifier = Modifier
                .width(segmentWidth)
                .fillMaxHeight()
                .offset(x = segmentWidth * indicatorOffsetFraction)
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.25f))
                .border(1.dp, accentColor.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
        )

        // Touch segments row
        Row(modifier = Modifier.fillMaxSize()) {
            // DEG Option
            val degTextColor by animateColorAsState(
                targetValue = if (isDegreeMode) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                label = "deg_color"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (!isDegreeMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggle(true)
                        }
                    }
                    .testTag("angle_mode_deg_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.deg_deg),
                    fontSize = 12.sp,
                    fontWeight = if (isDegreeMode) FontWeight.Bold else FontWeight.Medium,
                    color = degTextColor
                )
            }

            // RAD Option
            val radTextColor by animateColorAsState(
                targetValue = if (!isDegreeMode) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                label = "rad_color"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        if (isDegreeMode) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggle(false)
                        }
                    }
                    .testTag("angle_mode_rad_tab"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.rad_rad),
                    fontSize = 12.sp,
                    fontWeight = if (!isDegreeMode) FontWeight.Bold else FontWeight.Medium,
                    color = radTextColor
                )
            }
        }
    }
}

