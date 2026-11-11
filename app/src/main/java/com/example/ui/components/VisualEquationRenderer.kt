package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DisplayFormulaColor

/**
 * A specialized renderer for mathematical expressions in Jetpack Compose.
 * Supports visual formatting for fractions, roots, and exponents.
 */

sealed class MathNode {
    data class TextNode(val text: String) : MathNode()
    data class FractionNode(val numerator: MathNode, val denominator: MathNode) : MathNode()
    data class RootNode(val content: MathNode) : MathNode()
    data class PowerNode(val base: MathNode, val exponent: MathNode) : MathNode()
    data class GroupNode(val nodes: List<MathNode>) : MathNode()
}

@Composable
fun VisualEquationRenderer(
    expression: String,
    fontSize: TextUnit = 32.sp,
    color: Color = DisplayFormulaColor,
    modifier: Modifier = Modifier
) {
    val rootNode = parseExpression(expression)
    
    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        RenderNode(node = rootNode, fontSize = fontSize, color = color)
    }
}

@Composable
private fun RenderNode(node: MathNode, fontSize: TextUnit, color: Color) {
    when (node) {
        is MathNode.TextNode -> {
            Text(
                text = node.text,
                color = color,
                fontSize = fontSize,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End
            )
        }
        is MathNode.FractionNode -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                RenderNode(node = node.numerator, fontSize = fontSize * 0.8f, color = color)
                Spacer(modifier = Modifier.height(1.dp))
                Box(
                    modifier = Modifier
                        .height(1.5.dp)
                        .background(color)
                        .width(IntrinsicSize.Min)
                )
                Spacer(modifier = Modifier.height(1.dp))
                RenderNode(node = node.denominator, fontSize = fontSize * 0.8f, color = color)
            }
        }
        is MathNode.RootNode -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = "√",
                    fontSize = fontSize * 1.2f,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Column {
                    Box(
                        modifier = Modifier
                            .height(1.dp)
                            .background(color)
                            .fillMaxWidth()
                    )
                    Box(modifier = Modifier.padding(horizontal = 4.dp)) {
                        RenderNode(node = node.content, fontSize = fontSize, color = color)
                    }
                }
            }
        }
        is MathNode.PowerNode -> {
            Row(verticalAlignment = Alignment.Top) {
                RenderNode(node = node.base, fontSize = fontSize, color = color)
                Box(modifier = Modifier.offset(y = (-fontSize.value * 0.3f).dp)) {
                    RenderNode(node = node.exponent, fontSize = fontSize * 0.6f, color = color)
                }
            }
        }
        is MathNode.GroupNode -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                node.nodes.forEach { RenderNode(it, fontSize, color) }
            }
        }
    }
}

/**
 * A basic recursive parser for mathematical expressions.
 */
private fun parseExpression(expr: String): MathNode {
    if (expr.isEmpty()) return MathNode.TextNode("")

    // Replace basic symbols for better visualization
    var current = expr
        .replace("×", "*")
        .replace("÷", "/")
        .replace("−", "-")
    
    // Check for fractions: (numerator)/(denominator)
    // This is a simplified regex-like check for (..)/(..)
    if (current.startsWith("(") && current.contains(")/(")) {
        val mid = current.indexOf(")/(")
        val end = current.lastIndexOf(")")
        if (end == current.length - 1) {
            val numStr = current.substring(1, mid)
            val denStr = current.substring(mid + 3, end)
            return MathNode.FractionNode(parseExpression(numStr), parseExpression(denStr))
        }
    }

    val results = mutableListOf<MathNode>()
    var i = 0
    while (i < current.length) {
        val remaining = current.substring(i)
        when {
            remaining.startsWith("sqrt(") -> {
                val end = findClosingBracket(remaining, 5)
                val inside = remaining.substring(5, end)
                results.add(MathNode.RootNode(parseExpression(inside)))
                i += end + 1
            }
            remaining.startsWith("^") -> {
                val last = results.removeLastOrNull() ?: MathNode.TextNode("")
                // Exponent could be in brackets ^(...)
                if (i + 1 < current.length && current[i+1] == '(') {
                    val expRemaining = current.substring(i + 1)
                    val end = findClosingBracket(expRemaining, 1)
                    val inside = expRemaining.substring(1, end)
                    results.add(MathNode.PowerNode(last, parseExpression(inside)))
                    i += end + 2
                } else {
                    // Single char exponent
                    val exponent = if (i + 1 < current.length) current[i+1].toString() else ""
                    results.add(MathNode.PowerNode(last, MathNode.TextNode(exponent)))
                    i += 2
                }
            }
            else -> {
                // Find next special char: sqrt or ^
                var nextSpecial = -1
                val specialKeywords = listOf("sqrt(", "^")
                for (keyword in specialKeywords) {
                    val idx = remaining.indexOf(keyword)
                    if (idx != -1 && (nextSpecial == -1 || idx < nextSpecial)) {
                        nextSpecial = idx
                    }
                }
                
                val text = if (nextSpecial == -1) remaining else remaining.substring(0, nextSpecial)
                if (text.isNotEmpty()) {
                    // Replace back the operators for display
                    val displayText = text
                        .replace("*", "×")
                        .replace("/", "÷")
                        .replace("-", "−")
                    results.add(MathNode.TextNode(displayText))
                    i += text.length
                } else if (nextSpecial == -1) {
                    i++
                }
            }
        }
    }
    
    return if (results.size == 1) results[0] else MathNode.GroupNode(results)
}

private fun findClosingBracket(text: String, start: Int): Int {
    var count = 1
    for (i in start until text.length) {
        if (text[i] == '(') count++
        if (text[i] == ')') count--
        if (count == 0) return i
    }
    return text.length
}
