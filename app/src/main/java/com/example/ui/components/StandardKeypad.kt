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

    // Keypad Colors matching screenshot with dynamic Theme Engine accent
    val operatorBg = com.example.ui.theme.CyberAmberBg
    val operatorText = com.example.ui.theme.CyberAmber
    val specialBg = com.example.ui.theme.CyberPurpleBg
    val specialText = com.example.ui.theme.CyberPurple
    val numberBg = com.example.ui.theme.KeypadNumberBg
    val numberText = com.example.ui.theme.KeypadNumberText
    val clearBg = com.example.ui.theme.CyberCrimsonBg
    val clearText = com.example.ui.theme.CyberCrimson
    val equalsBg = accentColor
    val equalsText = Color(0xFF002530)

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
                shape = buttonShape,
                testTag = "btn_backspace",
                onClick = onBackspace
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "0",
                backgroundColor = numberBg,
                contentColor = numberText,
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
                fontSize = 28.sp,
                shape = buttonShape,
                testTag = "btn_decimal",
                onClick = onDecimalPoint
            )

            CalculatorButton(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                text = "=",
                backgroundColor = equalsBg,
                contentColor = equalsText,
                fontSize = 30.sp,
                shape = buttonShape,
                testTag = "btn_equals",
                onClick = onEquals
            )
        }
    }
}

