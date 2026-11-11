package com.example.data.units

import androidx.annotation.StringRes
import com.example.R
import java.math.BigDecimal
import java.math.RoundingMode

import com.example.data.repository.CurrencyRepository

enum class UnitCategory(@StringRes val titleResId: Int, val iconName: String) {
    CURRENCY(R.string.cat_currency, "AttachMoney"),
    LENGTH(R.string.cat_length, "Straighten"),
    MASS(R.string.cat_mass, "FitnessCenter"),
    TEMPERATURE(R.string.cat_temp, "Thermostat"),
    AREA(R.string.cat_area, "SquareFoot"),
    VOLUME(R.string.cat_volume, "WaterDrop"),
    SPEED(R.string.cat_speed, "Speed"),
    STORAGE(R.string.cat_storage, "Storage")
}

data class UnitItem(
    val id: String,
    @StringRes val nameResId: Int,
    val symbol: String,
    val factorToBase: Double // conversion factor relative to base unit of category
)

object UnitConverterEngine {

    val categories: Map<UnitCategory, List<UnitItem>> = mapOf(
        UnitCategory.CURRENCY to listOf(
            UnitItem("USD", R.string.unit_usd, "$", 1.0),
            UnitItem("EUR", R.string.unit_eur, "€", 1.0),
            UnitItem("GBP", R.string.unit_gbp, "£", 1.0),
            UnitItem("EGP", R.string.unit_egp, "EGP", 1.0),
            UnitItem("SAR", R.string.unit_sar, "SAR", 1.0),
            UnitItem("AED", R.string.unit_aed, "AED", 1.0),
            UnitItem("KWD", R.string.unit_kwd, "KWD", 1.0),
            UnitItem("QAR", R.string.unit_qar, "QAR", 1.0),
            UnitItem("JPY", R.string.unit_jpy, "¥", 1.0),
            UnitItem("CAD", R.string.unit_cad, "CA$", 1.0),
            UnitItem("AUD", R.string.unit_aud, "AU$", 1.0),
            UnitItem("CNY", R.string.unit_cny, "¥", 1.0),
            UnitItem("INR", R.string.unit_inr, "₹", 1.0),
            UnitItem("CHF", R.string.unit_chf, "CHF", 1.0)
        ),
        UnitCategory.LENGTH to listOf(
            UnitItem("m", R.string.unit_meter, "m", 1.0),
            UnitItem("cm", R.string.unit_cm, "cm", 0.01),
            UnitItem("mm", R.string.unit_mm, "mm", 0.001),
            UnitItem("km", R.string.unit_km, "km", 1000.0),
            UnitItem("inch", R.string.unit_inch, "in", 0.0254),
            UnitItem("foot", R.string.unit_foot, "ft", 0.3048),
            UnitItem("yard", R.string.unit_yard, "yd", 0.9144),
            UnitItem("mile", R.string.unit_mile, "mi", 1609.344)
        ),
        UnitCategory.MASS to listOf(
            UnitItem("kg", R.string.unit_kg, "kg", 1.0),
            UnitItem("g", R.string.unit_g, "g", 0.001),
            UnitItem("mg", R.string.unit_mg, "mg", 0.000001),
            UnitItem("ton", R.string.unit_ton, "t", 1000.0),
            UnitItem("lb", R.string.unit_lb, "lb", 0.45359237),
            UnitItem("oz", R.string.unit_oz, "oz", 0.028349523)
        ),
        UnitCategory.TEMPERATURE to listOf(
            UnitItem("celsius", R.string.unit_celsius, "°C", 1.0),
            UnitItem("fahrenheit", R.string.unit_fahrenheit, "°F", 1.0),
            UnitItem("kelvin", R.string.unit_kelvin, "K", 1.0)
        ),
        UnitCategory.AREA to listOf(
            UnitItem("sq_m", R.string.unit_sq_m, "m²", 1.0),
            UnitItem("sq_km", R.string.unit_sq_km, "km²", 1_000_000.0),
            UnitItem("sq_cm", R.string.unit_sq_cm, "cm²", 0.0001),
            UnitItem("hectare", R.string.unit_hectare, "ha", 10000.0),
            UnitItem("acre", R.string.unit_acre, "ac", 4046.856),
            UnitItem("sq_ft", R.string.unit_sq_ft, "ft²", 0.092903)
        ),
        UnitCategory.VOLUME to listOf(
            UnitItem("liter", R.string.unit_liter, "L", 1.0),
            UnitItem("ml", R.string.unit_ml, "mL", 0.001),
            UnitItem("cum", R.string.unit_cum, "m³", 1000.0),
            UnitItem("gallon_us", R.string.unit_gallon_us, "gal", 3.78541),
            UnitItem("cup", R.string.unit_cup, "cup", 0.24),
            UnitItem("fl_oz", R.string.unit_fl_oz, "fl oz", 0.0295735)
        ),
        UnitCategory.SPEED to listOf(
            UnitItem("kmh", R.string.unit_kmh, "km/h", 1.0),
            UnitItem("ms", R.string.unit_ms, "m/s", 3.6),
            UnitItem("mph", R.string.unit_mph, "mph", 1.60934),
            UnitItem("knot", R.string.unit_knot, "kn", 1.852)
        ),
        UnitCategory.STORAGE to listOf(
            UnitItem("byte", R.string.unit_byte, "B", 1.0),
            UnitItem("kb", R.string.unit_kb, "KB", 1024.0),
            UnitItem("mb", R.string.unit_mb, "MB", 1024.0 * 1024.0),
            UnitItem("gb", R.string.unit_gb, "GB", 1024.0 * 1024.0 * 1024.0),
            UnitItem("tb", R.string.unit_tb, "TB", 1024.0 * 1024.0 * 1024.0 * 1024.0)
        )
    )

    fun convert(
        category: UnitCategory,
        fromUnitId: String,
        toUnitId: String,
        value: Double
    ): Double {
        if (fromUnitId == toUnitId) return value

        if (category == UnitCategory.CURRENCY) {
            return CurrencyRepository.convertCurrency(fromUnitId, toUnitId, value)
        }

        if (category == UnitCategory.TEMPERATURE) {
            return convertTemperature(fromUnitId, toUnitId, value)
        }

        val units = categories[category] ?: return value
        val fromUnit = units.find { it.id == fromUnitId } ?: return value
        val toUnit = units.find { it.id == toUnitId } ?: return value

        // Convert fromUnit to base unit, then base unit to toUnit
        val baseValue = value * fromUnit.factorToBase
        return baseValue / toUnit.factorToBase
    }

    private fun convertTemperature(fromId: String, toId: String, value: Double): Double {
        // First convert to Celsius
        val celsius = when (fromId) {
            "celsius" -> value
            "fahrenheit" -> (value - 32.0) * 5.0 / 9.0
            "kelvin" -> value - 273.15
            else -> value
        }

        // Convert Celsius to destination
        return when (toId) {
            "celsius" -> celsius
            "fahrenheit" -> (celsius * 9.0 / 5.0) + 32.0
            "kelvin" -> celsius + 273.15
            else -> celsius
        }
    }

    fun formatNumber(value: Double): String {
        if (value.isNaN() || value.isInfinite()) return "0"
        if (kotlin.math.abs(value) < 1e-12) return "0"
        if (kotlin.math.abs(value) >= 1e10 || (kotlin.math.abs(value) > 0 && kotlin.math.abs(value) < 1e-4)) {
            return String.format(java.util.Locale.US, "%.5e", value)
        }
        val bd = BigDecimal(value).setScale(6, RoundingMode.HALF_UP).stripTrailingZeros()
        return bd.toPlainString()
    }
}
