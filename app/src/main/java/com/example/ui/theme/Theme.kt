package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue

val LocalAccentColor = compositionLocalOf { NeonCyan }
val LocalPowerSavingMode = compositionLocalOf { false }

@Composable
fun SmartCalculatorTheme(
    accentColor: Color = NeonCyan,
    backgroundTheme: com.example.ui.BackgroundTheme = com.example.ui.BackgroundTheme.CHARCOAL,
    isPowerSaving: Boolean = false,
    content: @Composable () -> Unit
) {
    // Distinct dark mode preset color palettes
    val targetColors = when (backgroundTheme) {
        com.example.ui.BackgroundTheme.CHARCOAL -> Triple(
            Color(0xFF12141A), // Deep Charcoal Background
            Color(0xFF1C202B), // Charcoal Surface
            Color(0xFF282E3D)  // Charcoal Surface Elevated
        )
        com.example.ui.BackgroundTheme.MIDNIGHT -> Triple(
            Color(0xFF090F1E), // Midnight Blue Background
            Color(0xFF152238), // Midnight Surface
            Color(0xFF21324E)  // Midnight Surface Elevated
        )
        com.example.ui.BackgroundTheme.PITCH_BLACK -> Triple(
            Color(0xFF000000), // Pure OLED Pitch Black Background
            Color(0xFF121212), // Pitch Black Surface
            Color(0xFF1E1E1E)  // Pitch Black Surface Elevated
        )
    }

    // Smooth cross-fade color transitions (400ms duration)
    val animatedBgColor by animateColorAsState(
        targetValue = targetColors.first,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "theme_bg_color"
    )
    val animatedSurfaceColor by animateColorAsState(
        targetValue = targetColors.second,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "theme_surface_color"
    )
    val animatedSurfaceElevatedColor by animateColorAsState(
        targetValue = targetColors.third,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "theme_surface_elevated_color"
    )
    val animatedAccentColor by animateColorAsState(
        targetValue = accentColor,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "theme_accent_color"
    )

    val colorScheme = darkColorScheme(
        primary = animatedAccentColor,
        onPrimary = Color.White,
        secondary = NeonOrange,
        onSecondary = Color.White,
        tertiary = NeonEmerald,
        onTertiary = Color.White,
        background = animatedBgColor,
        onBackground = Color.White,
        surface = animatedSurfaceColor,
        onSurface = Color.White,
        surfaceVariant = animatedSurfaceElevatedColor,
        onSurfaceVariant = DisplayFormulaColor,
        outline = DarkBorder,
        error = ErrorColor
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                WindowCompat.getInsetsController(window, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    CompositionLocalProvider(
        LocalAccentColor provides animatedAccentColor,
        LocalPowerSavingMode provides isPowerSaving
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
