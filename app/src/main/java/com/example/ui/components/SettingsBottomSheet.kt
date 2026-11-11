package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.DisplayResultColor
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsBottomSheet(
    selectedAccentIndex: Int,
    accentColors: List<Color>,
    backgroundTheme: com.example.ui.BackgroundTheme,
    isPowerSavingEnabled: Boolean,
    isSoundEnabled: Boolean = true,
    onSelectColor: (Int) -> Unit,
    onSelectBackgroundTheme: (com.example.ui.BackgroundTheme) -> Unit,
    onTogglePowerSaving: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val colorNames = listOf(
        com.example.R.string.color_neon_cyan,
        com.example.R.string.color_emerald,
        com.example.R.string.color_vibrant_orange,
        com.example.R.string.color_electric_purple,
        com.example.R.string.color_bright_pink,
        com.example.R.string.color_amber_yellow
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = androidx.compose.ui.res.stringResource(com.example.R.string.accent_color_settings),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DisplayResultColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = androidx.compose.ui.res.stringResource(com.example.R.string.choose_accent_color_hint),
                fontSize = 14.sp,
                color = DisplayFormulaColor
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp) // Reduced height to fit both
            ) {
                itemsIndexed(accentColors) { index, color ->
                    val isSelected = index == selectedAccentIndex
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) color.copy(alpha = 0.2f) else DarkSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) color else DisplayFormulaColor.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectColor(index) }
                            .padding(10.dp)
                            .testTag("accent_color_option_$index"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.selected),
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = if (index < colorNames.size) androidx.compose.ui.res.stringResource(colorNames[index]) else "Color $index",
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) DisplayResultColor else DisplayFormulaColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Background Theme Selection
            Text(
                text = androidx.compose.ui.res.stringResource(com.example.R.string.choose_theme_hint),
                fontSize = 14.sp,
                color = DisplayFormulaColor
            )
            Spacer(modifier = Modifier.height(12.dp))

            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(com.example.ui.BackgroundTheme.entries.toTypedArray()) { themeItem ->
                    val isSelected = backgroundTheme == themeItem
                    androidx.compose.material3.FilterChip(
                        selected = isSelected,
                        onClick = { onSelectBackgroundTheme(themeItem) },
                        label = {
                            Text(text = androidx.compose.ui.res.stringResource(themeItem.titleResId))
                        },
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                            selectedLabelColor = NeonCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Power Saving Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated.copy(alpha = 0.4f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.power_saving_toggle),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DisplayResultColor
                    )
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.power_saving_mode_desc),
                        fontSize = 11.sp,
                        color = DisplayFormulaColor.copy(alpha = 0.6f)
                    )
                }
                androidx.compose.material3.Switch(
                    checked = isPowerSavingEnabled,
                    onCheckedChange = onTogglePowerSaving,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = NeonCyan,
                        checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = DisplayFormulaColor.copy(alpha = 0.5f),
                        uncheckedTrackColor = DarkSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sound Effects Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated.copy(alpha = 0.4f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.sound_effects_toggle),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = DisplayResultColor
                    )
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.sound_effects_desc),
                        fontSize = 11.sp,
                        color = DisplayFormulaColor.copy(alpha = 0.6f)
                    )
                }
                androidx.compose.material3.Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = onToggleSound,
                    colors = androidx.compose.material3.SwitchDefaults.colors(
                        checkedThumbColor = NeonCyan,
                        checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
                        uncheckedThumbColor = DisplayFormulaColor.copy(alpha = 0.5f),
                        uncheckedTrackColor = DarkSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About Section
            Text(
                text = androidx.compose.ui.res.stringResource(com.example.R.string.about_app),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DisplayResultColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = androidx.compose.ui.res.stringResource(com.example.R.string.app_version_desc),
                fontSize = 12.sp,
                color = DisplayFormulaColor.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
