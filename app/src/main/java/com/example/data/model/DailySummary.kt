package com.example.data.model

data class DailySummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netBalance: Double = 0.0,
    val cashInHand: Double = 0.0,
    val bankUpiBalance: Double = 0.0,
    val transactionCount: Int = 0,
    val incomeCount: Int = 0,
    val expenseCount: Int = 0,
    val categoryTotals: Map<String, Double> = emptyMap(),
    val paymentModeTotals: Map<PaymentMode, Double> = emptyMap()
)

data class VleProfile(
    val centerName: String = "Jan Seva Kendra",
    val vleName: String = "CSC VLE",
    val cscId: String = "CSC12345678",
    val phone: String = "",
    val openingCash: Double = 0.0,
    val openingBank: Double = 0.0
)
