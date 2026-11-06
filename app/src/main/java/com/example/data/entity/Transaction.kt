package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionType

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val amount: Double,
    val paymentMode: String, // "CASH", "ONLINE_UPI", "BANK_TRANSFER"
    val customerName: String = "",
    val customerPhone: String = "",
    val referenceNo: String = "",
    val notes: String = "",
    val aepsCashGiven: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis(),
    val dateString: String = "" // "YYYY-MM-DD"
) {
    val transactionType: TransactionType
        get() = runCatching { TransactionType.valueOf(type) }.getOrDefault(TransactionType.INCOME)

    val mode: PaymentMode
        get() = runCatching { PaymentMode.valueOf(paymentMode) }.getOrDefault(PaymentMode.CASH)
}
