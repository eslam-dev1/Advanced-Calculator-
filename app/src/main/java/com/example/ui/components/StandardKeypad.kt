package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.KeypadClearBg
import com.example.ui.theme.KeypadClearText
import com.example.ui.theme.KeypadNumberBg
import com.example.ui.theme.KeypadNumberText
import com.example.ui.theme.KeypadOperatorBg
import com.example.ui.theme.KeypadOperatorText
import com.example.ui.theme.KeypadSpecialBg
import com.example.ui.theme.KeypadSpecialText
import com.example.ui.theme.LocalAccentColor

@Composable
fun StandardKeypad(
    onDigit: (String) -> Unit,
    onOperator: (String) -> Unit,
    onDecimalPoint: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onToggleSign: () -> Unit,
    onPercentage: () -> Unit,
    onEquals: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonSpacing = 10.dp
    val buttonShape = RoundedCornerShape(22.dp)
    val accentColor = LocalAccentColor.current

    // Cyber Keypad Colors strictly matching the reference design image
    val operatorBg = Color(0xFF26170A)
    val operatorBorder = Color(0xFFFF9800)
    val operatorText = Color(0xFFFFAB40)

    val specialBg = Color(0xFF1A1226)
    val specialBorder = Color(0xFFAB47BC)
    val specialText = Color(0xFFCE93D8)

    val numberBg = Color(0xFF121722)
    val numberBorder = Color(0xFF263248)
    val numberText = Color(0xFFF1F5F9)

    val clearBg = Color(0xFF2B0E14)
    val clearBorder = Color(0xFFFF3B5C)
    val clearText = Color(0xFFFF5252)

    val equalsBrush = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(
            accentColor,
            Color(0xFF00B0FF),
            Color(0xFF0091EA)
        )
    )
    val equalsBorder = Color(0xFF80D8FF)
    val equalsText = Color(0xFF00222C)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Row 1: AC (Clear), ± (Sign), % (Percent), ÷ (Divide)
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "AC",
                backgroundColor = clearBg,
                contentColor = clearText,
                borderColor = clearBorder,
                fontSize = 22.sp,
                shape = buttonShape,
                testTag = "btn_clear",
                onClick = onClear
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "±",
                backgroundColor = specialBg,
                contentColor = specialText,
                borderColor = specialBorder,
                fontSize = 24.sp,
                shape = buttonShape,
                testTag = "btn_toggle_sign",
                onClick = onToggleSign
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "%",
                backgroundColor = specialBg,
                contentColor = specialText,
                borderColor = specialBorder,
                fontSize = 22.sp,
                shape = buttonShape,
                testTag = "btn_percentage",
                onClick = onPercentage
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "÷",
                backgroundColor = operatorBg,
                contentColor = operatorText,
                borderColor = operatorBorder,
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_divide",
                onClick = { onOperator("÷") }
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "7",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_7",
                onClick = { onDigit("7") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "8",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_8",
                onClick = { onDigit("8") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "9",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_9",
                onClick = { onDigit("9") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "×",
                backgroundColor = operatorBg,
                contentColor = operatorText,
                borderColor = operatorBorder,
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_multiply",
                onClick = { onOperator("×") }
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "4",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_4",
                onClick = { onDigit("4") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "5",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_5",
                onClick = { onDigit("5") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "6",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_6",
                onClick = { onDigit("6") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "−",
                backgroundColor = operatorBg,
                contentColor = operatorText,
                borderColor = operatorBorder,
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_subtract",
                onClick = { onOperator("−") }
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "1",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_1",
                onClick = { onDigit("1") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "2",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_2",
                onClick = { onDigit("2") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "3",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_3",
                onClick = { onDigit("3") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "+",
                backgroundColor = operatorBg,
                contentColor = operatorText,
                borderColor = operatorBorder,
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_add",
                onClick = { onOperator("+") }
            )
        }

        // Row 5: ⌫ (Backspace), 0, . (Decimal), = (Equals)
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                icon = Icons.AutoMirrored.Filled.Backspace,
                backgroundColor = specialBg,
                contentColor = specialText,
                borderColor = specialBorder,
                shape = buttonShape,
                testTag = "btn_backspace",
                onClick = onBackspace
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "0",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 26.sp,
                shape = buttonShape,
                testTag = "btn_0",
                onClick = { onDigit("0") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = ".",
                backgroundColor = numberBg,
                contentColor = numberText,
                borderColor = numberBorder,
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_decimal",
                onClick = onDecimalPoint
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "=",
                backgroundBrush = equalsBrush,
                contentColor = equalsText,
                borderColor = equalsBorder,
                borderWidth = 1.6.dp,
                fontSize = 30.sp,
                shape = buttonShape,
                testTag = "btn_equals",
                onClick = onEquals
            )
        }
    }
}

