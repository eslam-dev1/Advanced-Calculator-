package com.example.data.programmer

enum class NumberBase(val radix: Int, val label: String, val shortName: String) {
    HEX(16, "سداسي عشري (HEX)", "HEX"),
    DEC(10, "عشري (DEC)", "DEC"),
    OCT(8, "ثماني (OCT)", "OCT"),
    BIN(2, "ثنائي (BIN)", "BIN")
}

data class BaseValues(
    val hex: String,
    val dec: String,
    val oct: String,
    val bin: String
)

object BaseConverterEngine {

    fun convertFrom(input: String, fromBase: NumberBase): BaseValues {
        val clean = input.trim().replace(" ", "")
        if (clean.isEmpty() || clean == "0") {
            return BaseValues("0", "0", "0", "0")
        }

        return try {
            val unsignedValue = when (fromBase) {
                NumberBase.HEX -> java.lang.Long.parseUnsignedLong(clean, 16)
                NumberBase.DEC -> clean.toLongOrNull() ?: java.lang.Long.parseUnsignedLong(clean, 10)
                NumberBase.OCT -> java.lang.Long.parseUnsignedLong(clean, 8)
                NumberBase.BIN -> java.lang.Long.parseUnsignedLong(clean, 2)
            }

            val hexStr = java.lang.Long.toHexString(unsignedValue).uppercase()
            val decStr = unsignedValue.toString()
            val octStr = java.lang.Long.toOctalString(unsignedValue)
            val rawBinStr = java.lang.Long.toBinaryString(unsignedValue)
            val formattedBin = formatBinaryWithNibbles(rawBinStr)

            BaseValues(
                hex = hexStr,
                dec = decStr,
                oct = octStr,
                bin = formattedBin
            )
        } catch (e: Exception) {
            BaseValues("خطأ", "خطأ", "خطأ", "خطأ")
        }
    }

    private fun formatBinaryWithNibbles(rawBin: String): String {
        val padded = if (rawBin.length % 4 != 0) {
            "0".repeat(4 - (rawBin.length % 4)) + rawBin
        } else {
            rawBin
        }
        return padded.chunked(4).joinToString(" ")
    }

    fun isCharAllowedForBase(char: Char, base: NumberBase): Boolean {
        return when (base) {
            NumberBase.BIN -> char == '0' || char == '1'
            NumberBase.OCT -> char in '0'..'7'
            NumberBase.DEC -> char in '0'..'9'
            NumberBase.HEX -> char in '0'..'9' || char in 'A'..'F' || char in 'a'..'f'
        }
    }
}
