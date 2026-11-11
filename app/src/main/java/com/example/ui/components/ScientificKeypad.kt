package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.CalculatorUiState
import com.example.ui.theme.LocalAccentColor

@Composable
fun ScientificKeypad(
    uiState: CalculatorUiState,
    onFunction: (String) -> Unit,
    onConstant: (String) -> Unit,
    onParenthesis: () -> Unit,
    onFactorial: () -> Unit,
    onPower: () -> Unit,
    onSquare: () -> Unit,
    onSqrt: () -> Unit,
    onToggleInverse: () -> Unit,
    onToggleHyp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isInv = uiState.isInverseActive
    val isHyp = uiState.isHypActive
    val accentColor = LocalAccentColor.current
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
    val activeBg = accentColor.copy(alpha = 0.3f)
    val activeContent = accentColor

    // Determine current trig function names based on INV and HYP
    val (sinLabel, sinFunc) = when {
        isHyp && isInv -> "asinh" to "asinh"
        isHyp -> "sinh" to "sinh"
        isInv -> "sin⁻¹" to "asin"
        else -> "sin" to "sin"
    }

    val (cosLabel, cosFunc) = when {
        isHyp && isInv -> "acosh" to "acosh"
        isHyp -> "cosh" to "cosh"
        isInv -> "cos⁻¹" to "acos"
        else -> "cos" to "cos"
    }

    val (tanLabel, tanFunc) = when {
        isHyp && isInv -> "atanh" to "atanh"
        isHyp -> "tanh" to "tanh"
        isInv -> "tan⁻¹" to "atan"
        else -> "tan" to "tan"
    }

    val buttonSpacing = 6.dp
    val buttonShape = RoundedCornerShape(14.dp)

    // Scientific theme colors strictly matching reference preview
    val cyanKeyBg = Color(0xFF0E1A26)
    val cyanKeyBorder = Color(0xFF00E5FF)
    val cyanKeyText = Color(0xFF00E5FF)

    val purpleKeyBg = Color(0xFF181126)
    val purpleKeyBorder = Color(0xFFAB47BC)
    val purpleKeyText = Color(0xFFCE93D8)

    val tealKeyBg = Color(0xFF0E1F24)
    val tealKeyBorder = Color(0xFF00BFA5)
    val tealKeyText = Color(0xFF64FFDA)

    val blueKeyBg = Color(0xFF0F1B2A)
    val blueKeyBorder = Color(0xFF00B0FF)
    val blueKeyText = Color(0xFF80D8FF)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        // Row 1: INV, hyp, sin, cos, tan
        Row(
            modifier = Modifier.fillMaxWidth().height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "inv",
                backgroundColor = if (isInv) accentColor.copy(alpha = 0.35f) else cyanKeyBg,
                contentColor = cyanKeyText,
                borderColor = cyanKeyBorder,
                borderWidth = 1.4.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_inv",
                onClick = onToggleInverse
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "hyp",
                backgroundColor = if (isHyp) accentColor.copy(alpha = 0.35f) else purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_hyp",
                onClick = onToggleHyp
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = sinLabel,
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_sin",
                onClick = { onFunction(sinFunc) }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = cosLabel,
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_cos",
                onClick = { onFunction(cosFunc) }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = tanLabel,
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_tan",
                onClick = { onFunction(tanFunc) }
            )
        }

        // Row 2: ln/e^x, log/10^x, log2, √, xʸ
        Row(
            modifier = Modifier.fillMaxWidth().height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = if (isInv) "eˣ" else "ln",
                backgroundColor = blueKeyBg,
                contentColor = blueKeyText,
                borderColor = blueKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_ln",
                onClick = {
                    if (isInv) {
                        onConstant("e")
                        onPower()
                    } else {
                        onFunction("ln")
                    }
                }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = if (isInv) "10ˣ" else "log",
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_log",
                onClick = {
                    if (isInv) {
                        onConstant("10")
                        onPower()
                    } else {
                        onFunction("log")
                    }
                }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = if (isInv) "x²" else "log₂",
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 13.sp,
                shape = buttonShape,
                testTag = "btn_log2",
                onClick = {
                    if (isInv) {
                        onSquare()
                    } else {
                        onFunction("log2")
                    }
                }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "√",
                backgroundColor = tealKeyBg,
                contentColor = tealKeyText,
                borderColor = tealKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 16.sp,
                shape = buttonShape,
                testTag = "btn_sqrt",
                onClick = onSqrt
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "xʸ",
                backgroundColor = tealKeyBg,
                contentColor = tealKeyText,
                borderColor = tealKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 15.sp,
                shape = buttonShape,
                testTag = "btn_power",
                onClick = onPower
            )
        }

        // Row 3: π, e, x!, (, )
        Row(
            modifier = Modifier.fillMaxWidth().height(46.dp),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "π",
                backgroundColor = tealKeyBg,
                contentColor = tealKeyText,
                borderColor = tealKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 16.sp,
                shape = buttonShape,
                testTag = "btn_pi",
                onClick = { onConstant("π") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "e",
                backgroundColor = tealKeyBg,
                contentColor = tealKeyText,
                borderColor = tealKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 16.sp,
                shape = buttonShape,
                testTag = "btn_e",
                onClick = { onConstant("e") }
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "x!",
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 15.sp,
                shape = buttonShape,
                testTag = "btn_factorial",
                onClick = onFactorial
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = "(",
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 16.sp,
                shape = buttonShape,
                testTag = "btn_open_paren",
                onClick = onParenthesis
            )

            CalculatorButton(
                modifier = Modifier.weight(1f),
                text = ")",
                backgroundColor = purpleKeyBg,
                contentColor = purpleKeyText,
                borderColor = purpleKeyBorder,
                borderWidth = 1.3.dp,
                fontSize = 16.sp,
                shape = buttonShape,
                testTag = "btn_close_paren",
                onClick = onParenthesis
            )
        }
    }
}

