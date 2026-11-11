package com.example.data.repository

import com.example.data.local.CalculationHistory
import com.example.data.local.HistoryDao
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<CalculationHistory>> = historyDao.getAllHistory()

    suspend fun addCalculation(expression: String, result: String) {
        historyDao.insert(CalculationHistory(expression = expression, result = result))
    }

    suspend fun deleteItem(item: CalculationHistory) {
        historyDao.delete(item)
    }

    suspend fun clearAll() {
        historyDao.clearAll()
    }
}
