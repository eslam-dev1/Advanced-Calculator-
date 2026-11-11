package com.example

import com.example.data.math.ExpressionEvaluator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `evaluate basic arithmetic with operator precedence`() {
        val result = ExpressionEvaluator.evaluate("5 + 3 × 2", isDegreeMode = true)
        assertTrue(result is ExpressionEvaluator.EvalResult.Success)
        assertEquals("11", (result as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate parentheses and nested calculations`() {
        val result = ExpressionEvaluator.evaluate("(5 + 3) × 2", isDegreeMode = true)
        assertTrue(result is ExpressionEvaluator.EvalResult.Success)
        assertEquals("16", (result as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate trigonometry in degrees with zero snapping`() {
        // Sine
        val sin90 = ExpressionEvaluator.evaluate("sin(90)", isDegreeMode = true)
        assertTrue(sin90 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (sin90 as ExpressionEvaluator.EvalResult.Success).formatted)

        val sin30 = ExpressionEvaluator.evaluate("sin(30)", isDegreeMode = true)
        assertTrue(sin30 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0.5", (sin30 as ExpressionEvaluator.EvalResult.Success).formatted)

        val sin0 = ExpressionEvaluator.evaluate("sin(0)", isDegreeMode = true)
        assertTrue(sin0 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0", (sin0 as ExpressionEvaluator.EvalResult.Success).formatted)

        // Cosine
        val cos60 = ExpressionEvaluator.evaluate("cos(60)", isDegreeMode = true)
        assertTrue(cos60 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0.5", (cos60 as ExpressionEvaluator.EvalResult.Success).formatted)

        val cos90 = ExpressionEvaluator.evaluate("cos(90)", isDegreeMode = true)
        assertTrue(cos90 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0", (cos90 as ExpressionEvaluator.EvalResult.Success).formatted)

        val cos0 = ExpressionEvaluator.evaluate("cos(0)", isDegreeMode = true)
        assertTrue(cos0 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (cos0 as ExpressionEvaluator.EvalResult.Success).formatted)

        // Tangent
        val tan45 = ExpressionEvaluator.evaluate("tan(45)", isDegreeMode = true)
        assertTrue(tan45 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (tan45 as ExpressionEvaluator.EvalResult.Success).formatted)

        val tan0 = ExpressionEvaluator.evaluate("tan(0)", isDegreeMode = true)
        assertTrue(tan0 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0", (tan0 as ExpressionEvaluator.EvalResult.Success).formatted)

        val tan90 = ExpressionEvaluator.evaluate("tan(90)", isDegreeMode = true)
        assertTrue(tan90 is ExpressionEvaluator.EvalResult.Error)
    }

    @Test
    fun `evaluate inverse trigonometry in degrees`() {
        val asinVal = ExpressionEvaluator.evaluate("asin(0.5)", isDegreeMode = true)
        assertTrue(asinVal is ExpressionEvaluator.EvalResult.Success)
        assertEquals("30", (asinVal as ExpressionEvaluator.EvalResult.Success).formatted)

        val acosVal = ExpressionEvaluator.evaluate("acos(0.5)", isDegreeMode = true)
        assertTrue(acosVal is ExpressionEvaluator.EvalResult.Success)
        assertEquals("60", (acosVal as ExpressionEvaluator.EvalResult.Success).formatted)

        val atanVal = ExpressionEvaluator.evaluate("atan(1)", isDegreeMode = true)
        assertTrue(atanVal is ExpressionEvaluator.EvalResult.Success)
        assertEquals("45", (atanVal as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate degree and radian mode difference`() {
        val degResult = ExpressionEvaluator.evaluate("sin(90)", isDegreeMode = true)
        assertTrue(degResult is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (degResult as ExpressionEvaluator.EvalResult.Success).formatted)

        val radResult = ExpressionEvaluator.evaluate("sin(π / 2)", isDegreeMode = false)
        assertTrue(radResult is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (radResult as ExpressionEvaluator.EvalResult.Success).formatted)

        val cosRad = ExpressionEvaluator.evaluate("cos(π)", isDegreeMode = false)
        assertTrue(cosRad is ExpressionEvaluator.EvalResult.Success)
        assertEquals("-1", (cosRad as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate logarithms log10 ln and log2`() {
        // Base 10 log
        val log100 = ExpressionEvaluator.evaluate("log(100)", isDegreeMode = true)
        assertTrue(log100 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("2", (log100 as ExpressionEvaluator.EvalResult.Success).formatted)

        val log1000 = ExpressionEvaluator.evaluate("log(1000)", isDegreeMode = true)
        assertTrue(log1000 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("3", (log1000 as ExpressionEvaluator.EvalResult.Success).formatted)

        // Natural log
        val lne = ExpressionEvaluator.evaluate("ln(e)", isDegreeMode = true)
        assertTrue(lne is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (lne as ExpressionEvaluator.EvalResult.Success).formatted)

        // Base 2 log
        val log2val = ExpressionEvaluator.evaluate("log2(8)", isDegreeMode = true)
        assertTrue(log2val is ExpressionEvaluator.EvalResult.Success)
        assertEquals("3", (log2val as ExpressionEvaluator.EvalResult.Success).formatted)

        // Log of negative or zero is error
        val logZero = ExpressionEvaluator.evaluate("log(0)", isDegreeMode = true)
        assertTrue(logZero is ExpressionEvaluator.EvalResult.Error)
    }

    @Test
    fun `evaluate hyperbolic functions`() {
        val sinh0 = ExpressionEvaluator.evaluate("sinh(0)", isDegreeMode = true)
        assertTrue(sinh0 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("0", (sinh0 as ExpressionEvaluator.EvalResult.Success).formatted)

        val cosh0 = ExpressionEvaluator.evaluate("cosh(0)", isDegreeMode = true)
        assertTrue(cosh0 is ExpressionEvaluator.EvalResult.Success)
        assertEquals("1", (cosh0 as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate powers roots and factorials`() {
        val powRoot = ExpressionEvaluator.evaluate("2^3 + sqrt(16)", isDegreeMode = true)
        assertTrue(powRoot is ExpressionEvaluator.EvalResult.Success)
        assertEquals("12", (powRoot as ExpressionEvaluator.EvalResult.Success).formatted)

        val fact = ExpressionEvaluator.evaluate("5!", isDegreeMode = true)
        assertTrue(fact is ExpressionEvaluator.EvalResult.Success)
        assertEquals("120", (fact as ExpressionEvaluator.EvalResult.Success).formatted)
    }

    @Test
    fun `evaluate live preview helper`() {
        val preview = ExpressionEvaluator.tryEvaluatePreview("15 × 4 + ", isDegreeMode = true)
        assertNotNull(preview)
        assertEquals("60", preview)
    }

    @Test
    fun `divide by zero returns error`() {
        val result = ExpressionEvaluator.evaluate("10 ÷ 0", isDegreeMode = true)
        assertTrue(result is ExpressionEvaluator.EvalResult.Error)
    }

    @Test
    fun `unit converter converts length and temperature correctly`() {
        val km = com.example.data.units.UnitConverterEngine.convert(
            com.example.data.units.UnitCategory.LENGTH,
            "m",
            "km",
            1000.0
        )
        assertEquals("1", com.example.data.units.UnitConverterEngine.formatNumber(km))

        val fahrenheit = com.example.data.units.UnitConverterEngine.convert(
            com.example.data.units.UnitCategory.TEMPERATURE,
            "celsius",
            "fahrenheit",
            100.0
        )
        assertEquals("212", com.example.data.units.UnitConverterEngine.formatNumber(fahrenheit))
    }

    @Test
    fun `programmer base converter converts decimal to hex and binary`() {
        val converted = com.example.data.programmer.BaseConverterEngine.convertFrom(
            "255",
            com.example.data.programmer.NumberBase.DEC
        )
        assertEquals("FF", converted.hex)
        assertEquals("1111 1111", converted.bin)
        assertEquals("377", converted.oct)
    }
}
