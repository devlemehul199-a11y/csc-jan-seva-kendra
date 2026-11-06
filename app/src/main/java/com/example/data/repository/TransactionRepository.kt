package com.example.data.repository

import com.example.data.db.TransactionDao
import com.example.data.model.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    fun getTransactionsByDate(dateString: String): Flow<List<TransactionEntity>> =
        dao.getTransactionsByDate(dateString)

    fun getTransactionsBetween(startTime: Long, endTime: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsBetween(startTime, endTime)

    suspend fun insert(transaction: TransactionEntity): Long = dao.insert(transaction)

    suspend fun insertAll(transactions: List<TransactionEntity>) = dao.insertAll(transactions)

    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.clearAll()

    suspend fun getAllSnapshot(): List<TransactionEntity> = dao.getAllTransactionsSnapshot()
}
