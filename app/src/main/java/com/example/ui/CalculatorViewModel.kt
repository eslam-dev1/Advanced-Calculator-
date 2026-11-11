package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.CalculationHistory
import com.example.data.local.CalculatorDatabase
import com.example.data.math.ExpressionEvaluator
import com.example.data.programmer.BaseConverterEngine
import com.example.data.programmer.BaseValues
import com.example.data.programmer.NumberBase
import com.example.data.repository.HistoryRepository
import com.example.data.units.UnitCategory
import com.example.data.units.UnitConverterEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.example.R

enum class CalculatorTab(@androidx.annotation.StringRes val titleResId: Int) {
    CALCULATOR(R.string.tab_calculator),
    CONVERTER(R.string.tab_converter),
    PROGRAMMER(R.string.tab_programmer),
    GRAPHER(R.string.tab_grapher)
}

enum class BackgroundTheme(@androidx.annotation.StringRes val titleResId: Int) {
    CHARCOAL(R.string.theme_charcoal),
    MIDNIGHT(R.string.theme_midnight),
    PITCH_BLACK(R.string.theme_pitch_black)
}

data class CalculatorUiState(
    val currentTab: CalculatorTab = CalculatorTab.CALCULATOR,
    val expression: String = "",
    val previewResult: String? = null,
    val finalResult: String? = null,
    val isDegreeMode: Boolean = true,
    val isScientificExpanded: Boolean = true,
    val isInverseActive: Boolean = false,
    val isHypActive: Boolean = false,
    val lastEvaluatedFormula: String? = null,
    val showHistory: Boolean = false,
    val historyList: List<CalculationHistory> = emptyList(),
    val errorMessage: String? = null,
    val toastMessage: String? = null,
    val showSettings: Boolean = false,
    val accentColorIndex: Int = 0,
    val backgroundTheme: BackgroundTheme = BackgroundTheme.CHARCOAL,

    // Memory functions (MC, MR, M+, M-, MS)
    val memoryValue: Double = 0.0,
    val hasMemory: Boolean = false,

    // Unit Converter State
    val unitCategory: UnitCategory = UnitCategory.LENGTH,
    val unitFromId: String = "m",
    val unitToId: String = "km",
    val unitInputValue: String = "1",
    val unitResultValue: String = "0.001",

    // Programmer Base Converter State
    val programmerBase: NumberBase = NumberBase.DEC,
    val programmerInput: String = "0",
    val programmerValues: BaseValues = BaseValues("0", "0", "0", "0"),

    // Grapher State
    val graphFunction: String = "sin(x)",
    val graphRangeX: Float = 10f,

    // PiP Mode State
    val isInPipMode: Boolean = false,

    // Mini Calculator Overlay Mode (draggable floating window over other views)
    val isMiniOverlayMode: Boolean = false,

    // Sound Feedback State
    val isSoundEnabled: Boolean = true,

    // Power Saving State (disabled by default to maintain full glowing UI)
    val isPowerSavingEnabled: Boolean = false,
    val isPowerSavingActive: Boolean = false,
    val powerSavingTimeoutMs: Long = 180_000L, // 3 minutes of inactivity if enabled
    val historySearchQuery: String = "",
    val isVoiceListening: Boolean = false,
    val isCurrencyLive: Boolean = false,
    val isCurrencyRefreshing: Boolean = false
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HistoryRepository
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    private val _historySearchQuery = MutableStateFlow("")
    private var idleJob: kotlinx.coroutines.Job? = null

    init {
        val db = CalculatorDatabase.getDatabase(application)
        repository = HistoryRepository(db.historyDao())

        viewModelScope.launch {
            combine(repository.allHistory, _historySearchQuery) { history, query ->
                if (query.isBlank()) {
                    history
                } else {
                    val q = query.trim()
                    history.filter {
                        it.expression.contains(q, ignoreCase = true) ||
                        it.result.contains(q, ignoreCase = true)
                    }
                }
            }.collect { filteredList ->
                _uiState.update { it.copy(historyList = filteredList) }
            }
        }
        resetIdleTimer()
        fetchCurrencyRates()
    }

    fun fetchCurrencyRates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCurrencyRefreshing = true) }
            val success = com.example.data.repository.CurrencyRepository.fetchLatestRates()
            _uiState.update { state ->
                val v = state.unitInputValue.toDoubleOrNull() ?: 0.0
                val converted = UnitConverterEngine.convert(state.unitCategory, state.unitFromId, state.unitToId, v)
                state.copy(
                    isCurrencyRefreshing = false,
                    isCurrencyLive = success,
                    unitResultValue = UnitConverterEngine.formatNumber(converted)
                )
            }
        }
    }

    fun onHistorySearchQueryChange(query: String) {
        _historySearchQuery.value = query
        _uiState.update { it.copy(historySearchQuery = query) }
    }

    fun onUserInteraction() {
        if (_uiState.value.isPowerSavingActive) {
            _uiState.update { it.copy(isPowerSavingActive = false) }
        }
        resetIdleTimer()
    }

    private fun resetIdleTimer() {
        idleJob?.cancel()
        if (!_uiState.value.isPowerSavingEnabled) return
        
        idleJob = viewModelScope.launch {
            kotlinx.coroutines.delay(_uiState.value.powerSavingTimeoutMs)
            _uiState.update { it.copy(isPowerSavingActive = true) }
        }
    }

    fun onToggleSoundEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isSoundEnabled = enabled) }
    }

    fun onTogglePowerSavingEnabled(enabled: Boolean) {
        _uiState.update { it.copy(isPowerSavingEnabled = enabled, isPowerSavingActive = false) }
        if (enabled) resetIdleTimer() else idleJob?.cancel()
    }

    fun onToggleVoiceListening(listening: Boolean) {
        _uiState.update { it.copy(isVoiceListening = listening) }
    }

    fun onVoiceResult(result: String) {
        _uiState.update { state ->
            val cleaned = cleanVoiceInput(result)
            val newExpr = if (state.finalResult != null) cleaned else state.expression + cleaned
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                isVoiceListening = false
            )
        }
        // Automatically evaluate if it looks like a complete equation
        if (_uiState.value.previewResult != null) {
            onEquals()
        }
    }

    private fun cleanVoiceInput(input: String): String {
        return input.lowercase()
            .replace(" plus ", "+")
            .replace(" minus ", "−")
            .replace(" times ", "×")
            .replace(" divided by ", "÷")
            .replace(" over ", "÷")
            .replace(" equals ", "=")
            .replace(" زائد ", "+")
            .replace(" ناقص ", "−")
            .replace(" في ", "×")
            .replace(" ضرب ", "×")
            .replace(" على ", "÷")
            .replace(" قسمة ", "÷")
            .replace(" يساوي ", "=")
            .replace(" mas ", "+")
            .replace(" menos ", "−")
            .replace(" por ", "×")
            .replace(" dividido por ", "÷")
            .replace(" entre ", "÷")
            .replace(" igual ", "=")
            .filter { it.isDigit() || it == '.' || it == '+' || it == '−' || it == '-' || it == '×' || it == '÷' || it == '(' || it == ')' || it == '^' || it == '%' || it == 'π' || it == 'e' }
    }

    fun onSelectTab(tab: CalculatorTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    // --- Calculator Standard & Scientific Actions ---

    fun onDigit(digit: String) {
        _uiState.update { state ->
            val newExpr = if (state.finalResult != null) {
                digit
            } else {
                state.expression + digit
            }
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                lastEvaluatedFormula = null,
                errorMessage = null
            )
        }
    }

    fun onDecimalPoint() {
        _uiState.update { state ->
            val expr = state.expression
            val lastToken = expr.takeLastWhile { it.isDigit() || it == '.' }
            if (lastToken.contains('.')) {
                return@update state
            }
            val newExpr = if (expr.isEmpty() || !expr.last().isDigit()) {
                "${expr}0."
            } else {
                "$expr."
            }
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onOperator(op: String) {
        _uiState.update { state ->
            var expr = state.expression
            if (state.finalResult != null) {
                expr = state.finalResult + op
            } else if (expr.isEmpty()) {
                if (op == "−" || op == "-") {
                    expr = "−"
                } else {
                    return@update state
                }
            } else {
                val lastChar = expr.last()
                if (isOperatorChar(lastChar)) {
                    expr = expr.dropLast(1) + op
                } else {
                    expr += op
                }
            }

            val preview = ExpressionEvaluator.tryEvaluatePreview(expr, state.isDegreeMode)
            state.copy(
                expression = expr,
                previewResult = preview,
                finalResult = null,
                lastEvaluatedFormula = null,
                errorMessage = null
            )
        }
    }

    fun onFunction(funcName: String) {
        _uiState.update { state ->
            val funcToken = "$funcName("
            val expr = if (state.finalResult != null && state.expression == state.finalResult) {
                funcToken
            } else {
                state.expression + funcToken
            }
            val preview = ExpressionEvaluator.tryEvaluatePreview(expr, state.isDegreeMode)
            state.copy(
                expression = expr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onConstant(constant: String) {
        _uiState.update { state ->
            val expr = if (state.finalResult != null && state.expression == state.finalResult) {
                constant
            } else {
                state.expression + constant
            }
            val preview = ExpressionEvaluator.tryEvaluatePreview(expr, state.isDegreeMode)
            state.copy(
                expression = expr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onParenthesis() {
        _uiState.update { state ->
            val expr = state.expression
            val openCount = expr.count { it == '(' }
            val closeCount = expr.count { it == ')' }

            val newChar = if (expr.isEmpty()) {
                "("
            } else {
                val last = expr.last()
                if (openCount > closeCount && (last.isDigit() || last == ')' || last == 'π' || last == 'e')) {
                    ")"
                } else if (isOperatorChar(last) || last == '(') {
                    "("
                } else {
                    "×("
                }
            }

            val newExpr = expr + newChar
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onFactorial() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val last = state.expression.last()
            if (!last.isDigit() && last != ')') return@update state

            val newExpr = state.expression + "!"
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onPower() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val newExpr = state.expression + "^"
            state.copy(
                expression = newExpr,
                previewResult = null,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onSquare() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val newExpr = state.expression + "^2"
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onSqrt() {
        onFunction("sqrt")
    }

    fun onToggleSign() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) {
                return@update state.copy(expression = "−")
            }

            val i = expr.indexOfLast { isOperatorChar(it) || it == '(' }
            val newExpr = if (i == -1) {
                if (expr.startsWith("−")) expr.drop(1) else "−$expr"
            } else {
                val prefix = expr.substring(0, i + 1)
                val suffix = expr.substring(i + 1)
                if (suffix.startsWith("−")) {
                    prefix + suffix.drop(1)
                } else {
                    "$prefix−$suffix"
                }
            }

            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onPercentage() {
        _uiState.update { state ->
            if (state.expression.isEmpty()) return@update state
            val last = state.expression.last()
            if (!last.isDigit() && last != ')') return@update state

            val newExpr = state.expression + "%"
            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = null,
                finalResult = null,
                lastEvaluatedFormula = null,
                errorMessage = null
            )
        }
    }

    fun onBackspace() {
        _uiState.update { state ->
            val expr = state.expression
            if (expr.isEmpty()) return@update state

            val functionNames = listOf(
                "asin(", "acos(", "atan(", "sinh(", "cosh(", "tanh(", "asinh(", "acosh(", "atanh(",
                "sin(", "cos(", "tan(", "sqrt(", "cbrt(", "log2(", "log(", "ln(", "exp(", "abs("
            )
            val matchingFunc = functionNames.find { expr.endsWith(it) }
            val newExpr = if (matchingFunc != null) {
                expr.dropLast(matchingFunc.length)
            } else {
                expr.dropLast(1)
            }

            val preview = ExpressionEvaluator.tryEvaluatePreview(newExpr, state.isDegreeMode)
            state.copy(
                expression = newExpr,
                previewResult = preview,
                finalResult = null,
                errorMessage = null
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        val expr = state.expression.trim()
        if (expr.isEmpty()) return

        when (val eval = ExpressionEvaluator.evaluate(expr, state.isDegreeMode)) {
            is ExpressionEvaluator.EvalResult.Success -> {
                val formatted = eval.formatted
                viewModelScope.launch {
                    repository.addCalculation(expr, formatted)
                }
                _uiState.update {
                    it.copy(
                        expression = expr,
                        previewResult = null,
                        finalResult = formatted,
                        lastEvaluatedFormula = expr,
                        errorMessage = null
                    )
                }
            }
            is ExpressionEvaluator.EvalResult.Failure -> {
                _uiState.update {
                    it.copy(
                        errorMessage = getErrorString(eval.error),
                        previewResult = null
                    )
                }
            }
        }
    }

    fun onToggleDegreeMode() {
        _uiState.update { state ->
            val newMode = !state.isDegreeMode
            val lastFormula = state.lastEvaluatedFormula
            if (lastFormula != null) {
                when (val eval = ExpressionEvaluator.evaluate(lastFormula, newMode)) {
                    is ExpressionEvaluator.EvalResult.Success -> {
                        state.copy(
                            isDegreeMode = newMode,
                            finalResult = eval.formatted,
                            previewResult = null,
                            errorMessage = null
                        )
                    }
                    is ExpressionEvaluator.EvalResult.Failure -> {
                        state.copy(
                            isDegreeMode = newMode,
                            errorMessage = getErrorString(eval.error)
                        )
                    }
                }
            } else {
                val preview = ExpressionEvaluator.tryEvaluatePreview(state.expression, newMode)
                state.copy(
                    isDegreeMode = newMode,
                    previewResult = preview
                )
            }
        }
    }

    fun onToggleScientific() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun onToggleInverse() {
        _uiState.update { it.copy(isInverseActive = !it.isInverseActive) }
    }

    fun onToggleHyp() {
        _uiState.update { it.copy(isHypActive = !it.isHypActive) }
    }

    fun onShowHistory(show: Boolean) {
        _uiState.update { it.copy(showHistory = show) }
    }

    fun onSelectHistory(item: CalculationHistory) {
        _uiState.update { state ->
            val preview = ExpressionEvaluator.tryEvaluatePreview(item.result, state.isDegreeMode)
            state.copy(
                expression = item.result,
                previewResult = preview,
                finalResult = item.result,
                showHistory = false,
                errorMessage = null
            )
        }
    }

    fun onDeleteHistoryItem(item: CalculationHistory) {
        viewModelScope.launch {
            repository.deleteItem(item)
        }
    }

    fun onClearAllHistory() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // --- Memory Operations (MC, MR, M+, M-, MS) ---

    fun onMemoryClear() {
        _uiState.update {
            it.copy(
                memoryValue = 0.0,
                hasMemory = false,
                toastMessage = getApplication<Application>().getString(R.string.memory_cleared)
            )
        }
    }

    fun onMemoryRecall() {
        val mem = _uiState.value.memoryValue
        val memStr = ExpressionEvaluator.formatResult(mem)
        _uiState.update { state ->
            val newExpr = if (state.finalResult != null) memStr else state.expression + memStr
            state.copy(
                expression = newExpr,
                finalResult = null,
                toastMessage = getApplication<Application>().getString(R.string.memory_recalled, memStr)
            )
        }
    }

    fun onMemoryStore() {
        val currVal = getCurrentNumericValue()
        if (currVal != null) {
            _uiState.update {
                it.copy(
                    memoryValue = currVal,
                    hasMemory = true,
                    toastMessage = getApplication<Application>().getString(R.string.memory_stored, ExpressionEvaluator.formatResult(currVal))
                )
            }
        }
    }

    fun onMemoryAdd() {
        val currVal = getCurrentNumericValue()
        if (currVal != null) {
            _uiState.update { state ->
                val newMem = state.memoryValue + currVal
                state.copy(
                    memoryValue = newMem,
                    hasMemory = true,
                    toastMessage = getApplication<Application>().getString(R.string.memory_added, ExpressionEvaluator.formatResult(newMem))
                )
            }
        }
    }

    fun onMemorySubtract() {
        val currVal = getCurrentNumericValue()
        if (currVal != null) {
            _uiState.update { state ->
                val newMem = state.memoryValue - currVal
                state.copy(
                    memoryValue = newMem,
                    hasMemory = true,
                    toastMessage = getApplication<Application>().getString(R.string.memory_subtracted, ExpressionEvaluator.formatResult(newMem))
                )
            }
        }
    }

    private fun getCurrentNumericValue(): Double? {
        val state = _uiState.value
        val source = state.finalResult ?: state.previewResult ?: state.expression
        return when (val eval = ExpressionEvaluator.evaluate(source, state.isDegreeMode)) {
            is ExpressionEvaluator.EvalResult.Success -> eval.value
            is ExpressionEvaluator.EvalResult.Failure -> null
        }
    }

    // --- Unit Converter Actions ---

    fun onSelectUnitCategory(category: UnitCategory) {
        val units = UnitConverterEngine.categories[category] ?: emptyList()
        val from = units.firstOrNull()?.id ?: ""
        val to = units.getOrNull(1)?.id ?: from
        val input = "1"
        val converted = UnitConverterEngine.convert(category, from, to, 1.0)
        _uiState.update {
            it.copy(
                unitCategory = category,
                unitFromId = from,
                unitToId = to,
                unitInputValue = input,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onSelectFromUnit(unitId: String) {
        _uiState.update { state ->
            val v = state.unitInputValue.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterEngine.convert(state.unitCategory, unitId, state.unitToId, v)
            state.copy(
                unitFromId = unitId,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onSelectToUnit(unitId: String) {
        _uiState.update { state ->
            val v = state.unitInputValue.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterEngine.convert(state.unitCategory, state.unitFromId, unitId, v)
            state.copy(
                unitToId = unitId,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onSwapUnits() {
        _uiState.update { state ->
            val newFrom = state.unitToId
            val newTo = state.unitFromId
            val v = state.unitInputValue.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterEngine.convert(state.unitCategory, newFrom, newTo, v)
            state.copy(
                unitFromId = newFrom,
                unitToId = newTo,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onUnitInput(digit: String) {
        _uiState.update { state ->
            val newInput = if (state.unitInputValue == "0" && digit != ".") {
                digit
            } else if (digit == "." && state.unitInputValue.contains(".")) {
                state.unitInputValue
            } else {
                state.unitInputValue + digit
            }
            val v = newInput.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterEngine.convert(state.unitCategory, state.unitFromId, state.unitToId, v)
            state.copy(
                unitInputValue = newInput,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onUnitBackspace() {
        _uiState.update { state ->
            val newInput = if (state.unitInputValue.length <= 1) "0" else state.unitInputValue.dropLast(1)
            val v = newInput.toDoubleOrNull() ?: 0.0
            val converted = UnitConverterEngine.convert(state.unitCategory, state.unitFromId, state.unitToId, v)
            state.copy(
                unitInputValue = newInput,
                unitResultValue = UnitConverterEngine.formatNumber(converted)
            )
        }
    }

    fun onUnitClear() {
        _uiState.update { state ->
            state.copy(
                unitInputValue = "0",
                unitResultValue = "0"
            )
        }
    }

    // --- Programmer Base Converter Actions ---

    fun onSelectProgrammerBase(base: NumberBase) {
        _uiState.update { state ->
            // Re-evaluate current input based on the new active base
            val currentDec = state.programmerValues.dec
            val newValues = BaseConverterEngine.convertFrom(currentDec, NumberBase.DEC)
            val newInput = when (base) {
                NumberBase.HEX -> newValues.hex
                NumberBase.DEC -> newValues.dec
                NumberBase.OCT -> newValues.oct
                NumberBase.BIN -> newValues.bin.replace(" ", "")
            }
            state.copy(
                programmerBase = base,
                programmerInput = newInput,
                programmerValues = newValues
            )
        }
    }

    fun onProgrammerInput(char: Char) {
        val state = _uiState.value
        if (!BaseConverterEngine.isCharAllowedForBase(char, state.programmerBase)) return

        _uiState.update { s ->
            val newInput = if (s.programmerInput == "0") char.toString() else s.programmerInput + char
            val values = BaseConverterEngine.convertFrom(newInput, s.programmerBase)
            s.copy(
                programmerInput = newInput,
                programmerValues = values
            )
        }
    }

    fun onProgrammerBackspace() {
        _uiState.update { s ->
            val newInput = if (s.programmerInput.length <= 1) "0" else s.programmerInput.dropLast(1)
            val values = BaseConverterEngine.convertFrom(newInput, s.programmerBase)
            s.copy(
                programmerInput = newInput,
                programmerValues = values
            )
        }
    }

    fun onProgrammerClear() {
        _uiState.update { s ->
            s.copy(
                programmerInput = "0",
                programmerValues = BaseValues("0", "0", "0", "0")
            )
        }
    }

    fun setToastMessage(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    val availableAccentColors = listOf(
        androidx.compose.ui.graphics.Color(0xFF00E5FF), // Electric Cyan
        androidx.compose.ui.graphics.Color(0xFFA855F7), // Midnight Purple
        androidx.compose.ui.graphics.Color(0xFF10B981), // Matrix Emerald
        androidx.compose.ui.graphics.Color(0xFFFF9100), // Solarized Dark / Amber
        androidx.compose.ui.graphics.Color(0xFFFF3355), // Cyber Rose
        androidx.compose.ui.graphics.Color(0xFFF59E0B)  // Golden Solar
    )

    fun onShowSettings(show: Boolean) {
        _uiState.update { it.copy(showSettings = show) }
    }

    fun onSelectAccentColor(index: Int) {
        _uiState.update { it.copy(accentColorIndex = index, showSettings = false, toastMessage = getApplication<Application>().getString(R.string.accent_changed)) }
    }

    fun onUpdateGraphFunction(func: String) {
        _uiState.update { it.copy(graphFunction = func) }
    }

    fun onZoomGraph(zoomIn: Boolean) {
        _uiState.update { state ->
            val newRange = if (zoomIn) {
                maxOf(2f, state.graphRangeX - 2f)
            } else {
                minOf(50f, state.graphRangeX + 2f)
            }
            state.copy(graphRangeX = newRange)
        }
    }

    fun onUpdatePipMode(inPip: Boolean) {
        _uiState.update { it.copy(isInPipMode = inPip) }
    }

    fun onToggleMiniOverlayMode(enabled: Boolean? = null) {
        _uiState.update { state ->
            val nextState = enabled ?: !state.isMiniOverlayMode
            state.copy(isMiniOverlayMode = nextState)
        }
    }

    fun onSelectBackgroundTheme(theme: BackgroundTheme) {
        _uiState.update { it.copy(backgroundTheme = theme, toastMessage = getApplication<Application>().getString(R.string.theme_changed)) }
    }

    private fun getErrorString(error: ExpressionEvaluator.EvalResult.Error): String {
        val resId = when (error) {
            ExpressionEvaluator.EvalResult.Error.SYNTAX -> R.string.error_syntax
            ExpressionEvaluator.EvalResult.Error.DIVIDE_BY_ZERO -> R.string.error_divide_by_zero
            ExpressionEvaluator.EvalResult.Error.ARITHMETIC -> R.string.error_arithmetic
            ExpressionEvaluator.EvalResult.Error.INCOMPLETE -> R.string.error_incomplete
            ExpressionEvaluator.EvalResult.Error.INVALID_FACTORIAL -> R.string.error_invalid_factorial
            ExpressionEvaluator.EvalResult.Error.NUMBER_ORDER -> R.string.error_number_order
            ExpressionEvaluator.EvalResult.Error.LOG_NEGATIVE -> R.string.error_log_negative
            ExpressionEvaluator.EvalResult.Error.UNDEFINED -> R.string.error_undefined
        }
        return getApplication<Application>().getString(resId)
    }

    private fun isOperatorChar(c: Char): Boolean {
        return c == '+' || c == '−' || c == '-' || c == '×' || c == '*' || c == '÷' || c == '/' || c == '^' || c == '%'
    }
}
