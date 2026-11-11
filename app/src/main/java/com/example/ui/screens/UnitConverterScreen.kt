package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.units.UnitCategory
import com.example.data.units.UnitConverterEngine
import com.example.ui.CalculatorUiState
import com.example.ui.components.CalculatorButton
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.DisplayResultColor
import com.example.ui.theme.KeypadClearBg
import com.example.ui.theme.KeypadClearText
import com.example.ui.theme.KeypadNumberBg
import com.example.ui.theme.KeypadNumberText
import com.example.ui.theme.KeypadSecondaryActionBg
import com.example.ui.theme.KeypadSecondaryActionText
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.ObsidianDark

import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator

@Composable
fun UnitConverterScreen(
    uiState: CalculatorUiState,
    onSelectCategory: (UnitCategory) -> Unit,
    onSelectFromUnit: (String) -> Unit,
    onSelectToUnit: (String) -> Unit,
    onSwapUnits: () -> Unit,
    onInputDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToast: (String) -> Unit,
    onRefreshCurrencyRates: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUnits = UnitConverterEngine.categories[uiState.unitCategory] ?: emptyList()
    val fromUnit = currentUnits.find { it.id == uiState.unitFromId } ?: currentUnits.firstOrNull()
    val toUnit = currentUnits.find { it.id == uiState.unitToId } ?: currentUnits.getOrNull(1)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Categories Horizontal Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(UnitCategory.entries.toTypedArray(), key = { it.name }) { cat ->
                val isSelected = cat == uiState.unitCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectCategory(cat) },
                    label = {
                        Text(
                            text = androidx.compose.ui.res.stringResource(cat.titleResId),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        selectedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("unit_category_${cat.name}")
                )
            }
        }

        // Live Currency Status Bar (When Currency Category is Selected)
        if (uiState.unitCategory == UnitCategory.CURRENCY) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isCurrencyLive) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = if (uiState.isCurrencyLive) com.example.ui.theme.NeonEmerald else DisplayFormulaColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = androidx.compose.ui.res.stringResource(
                            if (uiState.isCurrencyLive) com.example.R.string.currency_live_rates
                            else com.example.R.string.currency_offline_rates
                        ),
                        fontSize = 11.sp,
                        color = if (uiState.isCurrencyLive) com.example.ui.theme.NeonEmerald else DisplayFormulaColor
                    )
                }

                IconButton(
                    onClick = onRefreshCurrencyRates,
                    enabled = !uiState.isCurrencyRefreshing,
                    modifier = Modifier.size(28.dp)
                ) {
                    if (uiState.isCurrencyRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.refresh_rates),
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Conversion Cards
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "From" unit selector and value
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitSelectorDropdown(
                        units = currentUnits,
                        selectedUnitId = uiState.unitFromId,
                        onSelectUnit = onSelectFromUnit,
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.from)
                    )

                    Text(
                        text = uiState.unitInputValue,
                        color = DisplayResultColor,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Swap Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .clickable { onSwapUnits() }
                            .testTag("swap_units_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.swap_units),
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // "To" unit selector and converted result
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UnitSelectorDropdown(
                        units = currentUnits,
                        selectedUnitId = uiState.unitToId,
                        onSelectUnit = onSelectToUnit,
                        label = androidx.compose.ui.res.stringResource(com.example.R.string.to)
                    )

                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = uiState.unitResultValue,
                            color = NeonOrange,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.End
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val unitName = context.getString(toUnit?.nameResId ?: com.example.R.string.unit_meter)
                                val clip = ClipData.newPlainText("Converted Result", "${uiState.unitResultValue} ${toUnit?.symbol ?: ""}")
                                clipboard.setPrimaryClip(clip)
                                val copiedMsg = context.getString(com.example.R.string.copied_to_clipboard)
                                onToast("$copiedMsg: ${uiState.unitResultValue} ${toUnit?.symbol ?: ""}")
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.copy),
                                tint = DisplayFormulaColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Live Conversion Grid for other units in this category
        Text(
            text = androidx.compose.ui.res.stringResource(com.example.R.string.all_units_equations),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = DisplayFormulaColor
        )

        val inputVal = uiState.unitInputValue.toDoubleOrNull() ?: 0.0
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated.copy(alpha = 0.4f))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            currentUnits.forEach { u ->
                val converted = UnitConverterEngine.convert(uiState.unitCategory, uiState.unitFromId, u.id, inputVal)
                val formatted = UnitConverterEngine.formatNumber(converted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${androidx.compose.ui.res.stringResource(u.nameResId)} (${u.symbol})",
                        color = DisplayFormulaColor,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$formatted ${u.symbol}",
                        color = if (u.id == uiState.unitToId) NeonOrange else DisplayResultColor,
                        fontWeight = if (u.id == uiState.unitToId) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Dedicated Compact Number Keypad
        ConverterKeypad(
            onDigit = onInputDigit,
            onBackspace = onBackspace,
            onClear = onClear
        )
    }
}

@Composable
private fun UnitSelectorDropdown(
    units: List<com.example.data.units.UnitItem>,
    selectedUnitId: String,
    onSelectUnit: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedUnit = units.find { it.id == selectedUnitId } ?: units.firstOrNull()

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceElevated)
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val unitName = if (selectedUnit != null) androidx.compose.ui.res.stringResource(selectedUnit.nameResId) else ""
            Text(
                text = "$label $unitName (${selectedUnit?.symbol ?: ""})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = NeonCyan
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurface)
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${androidx.compose.ui.res.stringResource(unit.nameResId)} (${unit.symbol})",
                            color = if (unit.id == selectedUnitId) NeonCyan else DisplayResultColor
                        )
                    },
                    onClick = {
                        onSelectUnit(unit.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ConverterKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 7, 8, 9, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "7",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_7",
                onClick = { onDigit("7") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "8",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_8",
                onClick = { onDigit("8") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "9",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_9",
                onClick = { onDigit("9") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = KeypadSecondaryActionBg,
                contentColor = KeypadSecondaryActionText,
                testTag = "conv_btn_backspace",
                onClick = onBackspace
            )
        }

        // Row 2: 4, 5, 6, C
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "4",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_4",
                onClick = { onDigit("4") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "5",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_5",
                onClick = { onDigit("5") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "6",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_6",
                onClick = { onDigit("6") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "C",
                backgroundColor = KeypadClearBg,
                contentColor = KeypadClearText,
                testTag = "conv_btn_clear",
                onClick = onClear
            )
        }

        // Row 3: 1, 2, 3, .
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "1",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_1",
                onClick = { onDigit("1") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "2",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_2",
                onClick = { onDigit("2") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "3",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_3",
                onClick = { onDigit("3") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = ".",
                backgroundColor = KeypadSecondaryActionBg,
                contentColor = KeypadSecondaryActionText,
                testTag = "conv_btn_dot",
                onClick = { onDigit(".") }
            )
        }

        // Row 4: 0, 00
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(2f).height(46.dp),
                text = "0",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_0",
                onClick = { onDigit("0") }
            )
            CalculatorButton(
                modifier = Modifier.weight(1f).height(46.dp),
                text = "00",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                testTag = "conv_btn_00",
                onClick = { onDigit("00") }
            )
        }
    }
}
