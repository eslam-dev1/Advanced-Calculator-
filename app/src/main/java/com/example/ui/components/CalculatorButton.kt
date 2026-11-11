package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize

import androidx.compose.foundation.border

@Composable
fun CalculatorButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    backgroundColor: Color,
    contentColor: Color,
    fontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    testTag: String,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val view = androidx.compose.ui.platform.LocalView.current
    val isPowerSaving = com.example.ui.theme.LocalPowerSavingMode.current
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !isPowerSaving) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 450f),
        label = "button_scale"
    )

    val currentBgColor = if (isPressed) {
        backgroundColor.copy(alpha = 0.75f)
    } else {
        backgroundColor
    }

    val borderColor = if (isPressed) {
        contentColor.copy(alpha = 0.4f)
    } else {
        contentColor.copy(alpha = 0.12f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .scale(scale)
            .clip(shape)
            .background(currentBgColor)
            .border(1.dp, borderColor, shape)
            .pointerInput(isPowerSaving) {
                awaitEachGesture {
                    awaitFirstDown()
                    isPressed = true
                    if (!isPowerSaving) {
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.KEYBOARD_TAP)
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    }
                    val up = waitForUpOrCancellation()
                    isPressed = false
                    if (up != null) {
                        onClick()
                    }
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = contentColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                maxLines = 1
            )
        } else if (icon != null) {
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = testTag,
                tint = contentColor,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
