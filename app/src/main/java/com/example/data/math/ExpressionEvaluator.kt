package com.example.data.math

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.*

/**
 * Advanced mathematical expression parser and evaluator for scientific calculations.
 * Implements trigonometric functions (sin, cos, tan, asin, acos, atan),
 * hyperbolic functions (sinh, cosh, tanh), logarithms (log10, ln, log2),
 * powers, roots, factorials, and constants using Kotlin's Math library.
 */
object ExpressionEvaluator {

    sealed class EvalResult {
        data class Success(val value: Double, val formatted: String) : EvalResult()
        enum class Error {
            SYNTAX, DIVIDE_BY_ZERO, ARITHMETIC, INCOMPLETE, INVALID_FACTORIAL, NUMBER_ORDER, LOG_NEGATIVE, UNDEFINED
        }
        data class Failure(val error: Error) : EvalResult()
    }

    /**
     * Evaluates a mathematical expression string.
     * @param rawExpr The raw input expression from the UI
     * @param isDegreeMode Whether trigonometric functions use degrees (true) or radians (false)
     */
    fun evaluate(rawExpr: String, isDegreeMode: Boolean): EvalResult {
        val sanitized = sanitize(rawExpr)
        if (sanitized.isBlank()) {
            return EvalResult.Success(0.0, "0")
        }

        return try {
            val tokens = tokenize(sanitized)
            val postfix = infixToPostfix(tokens)
            val result = evaluatePostfix(postfix, isDegreeMode)
            if (result.isNaN() || result.isInfinite()) {
                EvalResult.Failure(EvalResult.Error.UNDEFINED)
            } else {
                EvalResult.Success(result, formatResult(result))
            }
        } catch (e: ArithmeticException) {
            when (e.message) {
                "DIVIDE_BY_ZERO" -> EvalResult.Failure(EvalResult.Error.DIVIDE_BY_ZERO)
                "INVALID_FACTORIAL" -> EvalResult.Failure(EvalResult.Error.INVALID_FACTORIAL)
                "LOG_NEGATIVE" -> EvalResult.Failure(EvalResult.Error.LOG_NEGATIVE)
                else -> EvalResult.Failure(EvalResult.Error.ARITHMETIC)
            }
        } catch (e: IllegalArgumentException) {
            when (e.message) {
                "INCOMPLETE" -> EvalResult.Failure(EvalResult.Error.INCOMPLETE)
                "NUMBER_ORDER" -> EvalResult.Failure(EvalResult.Error.NUMBER_ORDER)
                else -> EvalResult.Failure(EvalResult.Error.SYNTAX)
            }
        } catch (e: Exception) {
            EvalResult.Failure(EvalResult.Error.SYNTAX)
        }
    }

    /**
     * Safely attempts live evaluation for preview. Returns formatted result or null.
     */
    fun tryEvaluatePreview(rawExpr: String, isDegreeMode: Boolean): String? {
        val trimmed = rawExpr.trim()
        if (trimmed.isEmpty()) return null

        var attempt = sanitize(trimmed)
        if (attempt.isEmpty()) return null

        // Auto-close open parentheses for live preview
        val openCount = attempt.count { it == '(' }
        val closeCount = attempt.count { it == ')' }
        if (openCount > closeCount) {
            attempt += ")".repeat(openCount - closeCount)
        }

        // Drop trailing operators or open parenthesis for preview
        if (attempt.endsWith("+") || attempt.endsWith("-") || attempt.endsWith("*") ||
            attempt.endsWith("/") || attempt.endsWith("^") || attempt.endsWith("(")
        ) {
            attempt = attempt.dropLast(1).trim()
        }

        if (attempt.isEmpty()) return null

        return when (val res = evaluate(attempt, isDegreeMode)) {
            is EvalResult.Success -> res.formatted
            is EvalResult.Failure -> null
        }
    }

    /**
     * Replaces visual symbols with internal operator symbols.
     */
    fun sanitize(expr: String): String {
        return expr
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", "PI")
            .replace("e", "E")
            .replace(" ", "")
    }

    /**
     * Formats a floating-point number cleanly without trailing zeroes or precision artifacts.
     */
    fun formatResult(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "Error"
        if (abs(value) < 1e-12) return "0"

        // If the number is huge or microscopic, use scientific notation
        if (abs(value) >= 1e12 || (abs(value) > 0 && abs(value) < 1e-6)) {
            return String.format(java.util.Locale.US, "%.6e", value)
                .replace("e+", "E")
                .replace("e-", "E-")
        }

        // Clean rounding up to 10 decimal places to eliminate floating point imprecision (e.g. 0.1 + 0.2)
        val bd = BigDecimal(value).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }

    private fun isOperator(c: Char): Boolean = c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%'

    private fun precedence(op: String): Int {
        return when (op) {
            "+", "-" -> 1
            "*", "/", "%" -> 2
            "^" -> 3
            "u-" -> 4 // unary minus
            "!" -> 5
            else -> 0
        }
    }

    private fun isRightAssociative(op: String): Boolean = op == "^" || op == "u-"

    private fun isOperand(token: String?): Boolean {
        if (token == null) return false
        return token.toDoubleOrNull() != null || token == ")" || token == "PI" || token == "E" || token == "!"
    }

    /**
     * Tokenizes sanitized expression string.
     */
    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        var prevToken: String? = null

        while (i < expr.length) {
            val c = expr[i]

            when {
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    val num = sb.toString()
                    // Implicit multiplication check (e.g. )2 or PI2)
                    if (prevToken != null && (prevToken == ")" || prevToken == "PI" || prevToken == "E" || prevToken == "!")) {
                        tokens.add("*")
                    }
                    tokens.add(num)
                    prevToken = num
                    continue
                }

                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isLetter() || (expr[i] == '2' && sb.toString() == "log"))) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString()
                    // Implicit multiplication before constant or function (e.g. 5sin(30), 2PI)
                    if (isOperand(prevToken)) {
                        tokens.add("*")
                    }
                    tokens.add(word)
                    prevToken = word
                    continue
                }

                c == '(' -> {
                    // Implicit multiplication before open paren (e.g. 5(2), )(3))
                    if (isOperand(prevToken)) {
                        tokens.add("*")
                    }
                    tokens.add("(")
                    prevToken = "("
                    i++
                }

                c == ')' -> {
                    tokens.add(")")
                    prevToken = ")"
                    i++
                }

                c == '-' -> {
                    // Check if unary minus
                    if (prevToken == null || prevToken == "(" || isOperator(prevToken.first())) {
                        tokens.add("u-")
                        prevToken = "u-"
                    } else {
                        tokens.add("-")
                        prevToken = "-"
                    }
                    i++
                }

                c == '!' -> {
                    tokens.add("!")
                    prevToken = "!"
                    i++
                }

                isOperator(c) -> {
                    tokens.add(c.toString())
                    prevToken = c.toString()
                    i++
                }

                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    /**
     * Converts Infix token stream to Postfix (RPN) using Dijkstra's Shunting-yard algorithm.
     */
    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = ArrayDeque<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null || token == "PI" || token == "E" -> {
                    output.add(token)
                }

                isFunction(token) -> {
                    stack.addLast(token)
                }

                token == "!" -> {
                    output.add(token)
                }

                token == "(" -> {
                    stack.addLast(token)
                }

                token == ")" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        output.add(stack.removeLast())
                    }
                    if (stack.isNotEmpty() && stack.last() == "(") {
                        stack.removeLast()
                    }
                    if (stack.isNotEmpty() && isFunction(stack.last())) {
                        output.add(stack.removeLast())
                    }
                }

                isOperator(token[0]) || token == "u-" -> {
                    while (stack.isNotEmpty() && stack.last() != "(") {
                        val top = stack.last()
                        val precTop = precedence(top)
                        val precCurr = precedence(token)

                        if ((!isRightAssociative(token) && precCurr <= precTop) ||
                            (isRightAssociative(token) && precCurr < precTop)
                        ) {
                            output.add(stack.removeLast())
                        } else {
                            break
                        }
                    }
                    stack.addLast(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.removeLast()
            if (top != "(" && top != ")") {
                output.add(top)
            }
        }

        return output
    }

    private fun isFunction(name: String): Boolean {
        return when (name) {
            "sin", "cos", "tan", "asin", "acos", "atan",
            "sinh", "cosh", "tanh", "asinh", "acosh", "atanh",
            "ln", "log", "log2", "exp", "sqrt", "cbrt", "abs" -> true
            else -> false
        }
    }

    /**
     * Evaluates Postfix (RPN) tokens with accurate scientific functions from kotlin.math.
     */
    private fun evaluatePostfix(postfix: List<String>, isDegreeMode: Boolean): Double {
        val stack = ArrayDeque<Double>()

        for (token in postfix) {
            when {
                token == "PI" -> stack.addLast(Math.PI)
                token == "E" -> stack.addLast(Math.E)
                token.toDoubleOrNull() != null -> stack.addLast(token.toDouble())

                token == "u-" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("INCOMPLETE")
                    stack.addLast(-stack.removeLast())
                }

                token == "!" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("INCOMPLETE")
                    val a = stack.removeLast()
                    if (a < 0 || a != floor(a) || a > 170) {
                        throw ArithmeticException("INVALID_FACTORIAL")
                    }
                    stack.addLast(factorial(a.toInt()))
                }

                isFunction(token) -> {
                    if (stack.isEmpty()) throw IllegalArgumentException("INCOMPLETE")
                    val arg = stack.removeLast()
                    val res = evaluateScientificFunction(token, arg, isDegreeMode)
                    stack.addLast(res)
                }

                isOperator(token[0]) -> {
                    if (stack.size < 2) throw IllegalArgumentException("INCOMPLETE")
                    val b = stack.removeLast()
                    val a = stack.removeLast()
                    val res = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> {
                            if (abs(b) < 1e-15) throw ArithmeticException("DIVIDE_BY_ZERO")
                            a / b
                        }
                        "%" -> (a * b) / 100.0
                        "^" -> a.pow(b)
                        else -> throw IllegalArgumentException("UNKNOWN")
                    }
                    stack.addLast(res)
                }
            }
        }

        if (stack.size != 1) {
            throw IllegalArgumentException("NUMBER_ORDER")
        }

        return stack.removeLast()
    }

    /**
     * Evaluates trigonometric and scientific functions with exact precision and zero-snapping.
     */
    private fun evaluateScientificFunction(func: String, arg: Double, isDegreeMode: Boolean): Double {
        return when (func) {
            // Sine
            "sin" -> {
                if (isDegreeMode) {
                    val deg = ((arg % 360.0) + 360.0) % 360.0
                    when {
                        deg == 0.0 || deg == 180.0 || deg == 360.0 -> 0.0
                        deg == 90.0 -> 1.0
                        deg == 270.0 -> -1.0
                        deg == 30.0 || deg == 150.0 -> 0.5
                        deg == 210.0 || deg == 330.0 -> -0.5
                        else -> sin(Math.toRadians(arg))
                    }
                } else {
                    sin(arg)
                }
            }

            // Cosine
            "cos" -> {
                if (isDegreeMode) {
                    val deg = ((arg % 360.0) + 360.0) % 360.0
                    when {
                        deg == 90.0 || deg == 270.0 -> 0.0
                        deg == 0.0 || deg == 360.0 -> 1.0
                        deg == 180.0 -> -1.0
                        deg == 60.0 || deg == 300.0 -> 0.5
                        deg == 120.0 || deg == 240.0 -> -0.5
                        else -> cos(Math.toRadians(arg))
                    }
                } else {
                    cos(arg)
                }
            }

            // Tangent
            "tan" -> {
                if (isDegreeMode) {
                    val deg = ((arg % 360.0) + 360.0) % 360.0
                    when {
                        deg == 90.0 || deg == 270.0 -> throw ArithmeticException("UNDEFINED")
                        deg == 0.0 || deg == 180.0 || deg == 360.0 -> 0.0
                        deg == 45.0 || deg == 225.0 -> 1.0
                        deg == 135.0 || deg == 315.0 -> -1.0
                        else -> tan(Math.toRadians(arg))
                    }
                } else {
                    val cosVal = cos(arg)
                    if (abs(cosVal) < 1e-15) throw ArithmeticException("UNDEFINED")
                    tan(arg)
                }
            }

            // Inverse Sine
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("ARITHMETIC")
                val rad = asin(arg)
                if (isDegreeMode) Math.toDegrees(rad) else rad
            }

            // Inverse Cosine
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("ARITHMETIC")
                val rad = acos(arg)
                if (isDegreeMode) Math.toDegrees(rad) else rad
            }

            // Inverse Tangent
            "atan" -> {
                val rad = atan(arg)
                if (isDegreeMode) Math.toDegrees(rad) else rad
            }

            // Hyperbolic functions
            "sinh" -> sinh(arg)
            "cosh" -> cosh(arg)
            "tanh" -> tanh(arg)
            "asinh" -> ln(arg + sqrt(arg * arg + 1.0))
            "acosh" -> {
                if (arg < 1.0) throw ArithmeticException("ARITHMETIC")
                ln(arg + sqrt(arg * arg - 1.0))
            }
            "atanh" -> {
                if (arg <= -1.0 || arg >= 1.0) throw ArithmeticException("ARITHMETIC")
                0.5 * ln((1.0 + arg) / (1.0 - arg))
            }

            // Logarithm base 10
            "log" -> {
                if (arg <= 0.0) throw ArithmeticException("LOG_NEGATIVE")
                log10(arg)
            }

            // Natural Logarithm ln
            "ln" -> {
                if (arg <= 0.0) throw ArithmeticException("LOG_NEGATIVE")
                ln(arg)
            }

            // Binary Logarithm
            "log2" -> {
                if (arg <= 0.0) throw ArithmeticException("LOG_NEGATIVE")
                log2(arg)
            }

            // Exponential e^x
            "exp" -> exp(arg)

            // Square root
            "sqrt" -> {
                if (arg < 0.0) throw ArithmeticException("ARITHMETIC")
                sqrt(arg)
            }

            // Cube root
            "cbrt" -> cbrt(arg)

            // Absolute value
            "abs" -> abs(arg)

            else -> throw IllegalArgumentException("SYNTAX")
        }
    }

    private fun factorial(n: Int): Double {
        if (n == 0 || n == 1) return 1.0
        var res = 1.0
        for (i in 2..n) {
            res *= i
        }
        return res
    }
}
