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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.programmer.BaseConverterEngine
import com.example.data.programmer.NumberBase
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
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.ObsidianDark

@Composable
fun BaseConverterScreen(
    uiState: CalculatorUiState,
    onSelectBase: (NumberBase) -> Unit,
    onInputChar: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Card: All 4 Bases Display
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
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // HEX Row
                BaseDisplayRow(
                    base = NumberBase.HEX,
                    value = uiState.programmerValues.hex,
                    isSelected = uiState.programmerBase == NumberBase.HEX,
                    onSelect = { onSelectBase(NumberBase.HEX) },
                    onCopy = {
                        val clip = ClipData.newPlainText("HEX", uiState.programmerValues.hex)
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        val msg = context.getString(com.example.R.string.copied_to_clipboard)
                        val baseName = context.getString(com.example.R.string.hex_full)
                        onToast("$msg ($baseName)")
                    }
                )

                // DEC Row
                BaseDisplayRow(
                    base = NumberBase.DEC,
                    value = uiState.programmerValues.dec,
                    isSelected = uiState.programmerBase == NumberBase.DEC,
                    onSelect = { onSelectBase(NumberBase.DEC) },
                    onCopy = {
                        val clip = ClipData.newPlainText("DEC", uiState.programmerValues.dec)
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        val msg = context.getString(com.example.R.string.copied_to_clipboard)
                        val baseName = context.getString(com.example.R.string.dec_full)
                        onToast("$msg ($baseName)")
                    }
                )

                // OCT Row
                BaseDisplayRow(
                    base = NumberBase.OCT,
                    value = uiState.programmerValues.oct,
                    isSelected = uiState.programmerBase == NumberBase.OCT,
                    onSelect = { onSelectBase(NumberBase.OCT) },
                    onCopy = {
                        val clip = ClipData.newPlainText("OCT", uiState.programmerValues.oct)
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        val msg = context.getString(com.example.R.string.copied_to_clipboard)
                        val baseName = context.getString(com.example.R.string.oct_full)
                        onToast("$msg ($baseName)")
                    }
                )

                // BIN Row
                BaseDisplayRow(
                    base = NumberBase.BIN,
                    value = uiState.programmerValues.bin,
                    isSelected = uiState.programmerBase == NumberBase.BIN,
                    onSelect = { onSelectBase(NumberBase.BIN) },
                    onCopy = {
                        val clip = ClipData.newPlainText("BIN", uiState.programmerValues.bin)
                        (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                        val msg = context.getString(com.example.R.string.copied_to_clipboard)
                        val baseName = context.getString(com.example.R.string.bin_full)
                        onToast("$msg ($baseName)")
                    }
                )
            }
        }

        // Adaptive Programmer Keypad
        ProgrammerKeypad(
            activeBase = uiState.programmerBase,
            onInputChar = onInputChar,
            onBackspace = onBackspace,
            onClear = onClear
        )
    }
}

@Composable
private fun BaseDisplayRow(
    base: NumberBase,
    value: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCopy: () -> Unit
) {
    val borderColor = if (isSelected) NeonCyan else DarkBorder
    val bgColor = if (isSelected) NeonCyan.copy(alpha = 0.1f) else DarkSurfaceElevated.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = base.shortName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) NeonCyan else DisplayFormulaColor,
            modifier = Modifier.size(width = 38.dp, height = 20.dp)
        )

        Text(
            text = value,
            fontSize = if (base == NumberBase.BIN) 12.sp else 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontFamily = FontFamily.Monospace,
            color = if (isSelected) DisplayResultColor else DisplayFormulaColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
        )

        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = androidx.compose.ui.res.stringResource(com.example.R.string.copy),
                tint = DisplayFormulaColor,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun ProgrammerKeypad(
    activeBase: NumberBase,
    onInputChar: (Char) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val isHexActive = activeBase == NumberBase.HEX

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1: A, B, C, D, E, F (for HEX)
        Row(
            modifier = Modifier.fillMaxWidth().height(42.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf('A', 'B', 'C', 'D', 'E', 'F').forEach { ch ->
                val enabled = BaseConverterEngine.isCharAllowedForBase(ch, activeBase)
                CalculatorButton(
                    modifier = Modifier.weight(1f),
                    text = ch.toString(),
                    backgroundColor = if (enabled) KeypadSecondaryActionBg else KeypadSecondaryActionBg.copy(alpha = 0.2f),
                    contentColor = if (enabled) NeonCyan else DisplayFormulaColor.copy(alpha = 0.2f),
                    fontSize = 15.sp,
                    testTag = "prog_btn_$ch",
                    onClick = { if (enabled) onInputChar(ch) }
                )
            }
        }

        // Row 2: 7, 8, 9, Backspace
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf('7', '8', '9').forEach { ch ->
                val enabled = BaseConverterEngine.isCharAllowedForBase(ch, activeBase)
                CalculatorButton(
                    modifier = Modifier.weight(1f),
                    text = ch.toString(),
                    backgroundColor = if (enabled) KeypadNumberBg else KeypadNumberBg.copy(alpha = 0.2f),
                    contentColor = if (enabled) KeypadNumberText else DisplayFormulaColor.copy(alpha = 0.2f),
                    fontSize = 20.sp,
                    testTag = "prog_btn_$ch",
                    onClick = { if (enabled) onInputChar(ch) }
                )
            }
            CalculatorButton(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = KeypadSecondaryActionBg,
                contentColor = KeypadSecondaryActionText,
                testTag = "prog_btn_backspace",
                onClick = onBackspace
            )
        }

        // Row 3: 4, 5, 6, AC
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf('4', '5', '6').forEach { ch ->
                val enabled = BaseConverterEngine.isCharAllowedForBase(ch, activeBase)
                CalculatorButton(
                    modifier = Modifier.weight(1f),
                    text = ch.toString(),
                    backgroundColor = if (enabled) KeypadNumberBg else KeypadNumberBg.copy(alpha = 0.2f),
                    contentColor = if (enabled) KeypadNumberText else DisplayFormulaColor.copy(alpha = 0.2f),
                    fontSize = 20.sp,
                    testTag = "prog_btn_$ch",
                    onClick = { if (enabled) onInputChar(ch) }
                )
            }
            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "AC",
                backgroundColor = KeypadClearBg,
                contentColor = KeypadClearText,
                fontSize = 18.sp,
                testTag = "prog_btn_clear",
                onClick = onClear
            )
        }

        // Row 4: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf('1', '2', '3').forEach { ch ->
                val enabled = BaseConverterEngine.isCharAllowedForBase(ch, activeBase)
                CalculatorButton(
                    modifier = Modifier.weight(1f),
                    text = ch.toString(),
                    backgroundColor = if (enabled) KeypadNumberBg else KeypadNumberBg.copy(alpha = 0.2f),
                    contentColor = if (enabled) KeypadNumberText else DisplayFormulaColor.copy(alpha = 0.2f),
                    fontSize = 20.sp,
                    testTag = "prog_btn_$ch",
                    onClick = { if (enabled) onInputChar(ch) }
                )
            }
            // 0 button (part of row 4/5)
            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "0",
                backgroundColor = KeypadNumberBg,
                contentColor = KeypadNumberText,
                fontSize = 20.sp,
                testTag = "prog_btn_0",
                onClick = { onInputChar('0') }
            )
        }
    }
}
