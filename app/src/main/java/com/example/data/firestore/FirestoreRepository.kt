package com.example.data.firestore

import android.content.Context
import com.example.R
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.VleProfile
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class FirestoreRepository(
    private val db: FirebaseFirestore
) {
    companion object {
        fun create(context: Context): FirestoreRepository {
            val dbId = context.getString(R.string.firestore_database_id)
            val firestore = FirebaseFirestore.getInstance(dbId)
            return FirestoreRepository(firestore)
        }
    }

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User is not signed in")
    }

    suspend fun saveUserProfile(profile: VleProfile) {
        val uid = requireUserId()
        val user = auth.currentUser
        val userDoc = db.collection("users").document(uid)

        val payload = hashMapOf<String, Any>(
            "userId" to uid,
            "email" to (user?.email ?: ""),
            "displayName" to (user?.displayName ?: profile.vleName),
            "centerName" to profile.centerName,
            "vleName" to profile.vleName,
            "cscId" to profile.cscId,
            "phone" to profile.phone,
            "openingCash" to profile.openingCash,
            "openingBank" to profile.openingBank,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        userDoc.set(payload, SetOptions.merge()).await()
    }

    suspend fun fetchUserProfile(): VleProfile? {
        val uid = requireUserId()
        val snapshot = db.collection("users").document(uid).get().await()
        if (!snapshot.exists()) return null

        return VleProfile(
            centerName = snapshot.getString("centerName") ?: "Jan Seva Kendra",
            vleName = snapshot.getString("vleName") ?: (auth.currentUser?.displayName ?: "VLE"),
            cscId = snapshot.getString("cscId") ?: "",
            phone = snapshot.getString("phone") ?: "",
            openingCash = snapshot.getDouble("openingCash") ?: 0.0,
            openingBank = snapshot.getDouble("openingBank") ?: 0.0
        )
    }

    suspend fun syncTransactionsToCloud(
        transactions: List<TransactionEntity>,
        onProgress: (Int, Int) -> Unit = { _, _ -> }
    ): Int {
        val uid = requireUserId()
        val collection = db.collection("users").document(uid).collection("transactions")
        var count = 0

        for ((index, tx) in transactions.withIndex()) {
            val docId = "tx_${tx.id}_${tx.timestamp}"
            val payload = hashMapOf<String, Any>(
                "userId" to uid,
                "type" to tx.type,
                "category" to tx.category,
                "amount" to tx.amount,
                "paymentMode" to tx.paymentMode,
                "customerName" to tx.customerName,
                "customerPhone" to tx.customerPhone,
                "referenceNo" to tx.referenceNo,
                "notes" to tx.notes,
                "aepsCashGiven" to tx.aepsCashGiven,
                "dateString" to tx.dateString,
                "createdAt" to Timestamp(tx.timestamp / 1000, ((tx.timestamp % 1000) * 1000000).toInt()),
                "updatedAt" to FieldValue.serverTimestamp()
            )

            collection.document(docId).set(payload, SetOptions.merge()).await()
            count++
            onProgress(index + 1, transactions.size)
        }
        return count
    }

    suspend fun fetchTransactionsFromCloud(): List<TransactionEntity> {
        val uid = requireUserId()
        val snapshot = db.collection("users").document(uid).collection("transactions")
            .orderBy("createdAt")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val typeStr = doc.getString("type") ?: "INCOME"
            val category = doc.getString("category") ?: "Other"
            val amount = doc.getDouble("amount") ?: 0.0
            val paymentMode = doc.getString("paymentMode") ?: "CASH"
            val customerName = doc.getString("customerName") ?: ""
            val customerPhone = doc.getString("customerPhone") ?: ""
            val referenceNo = doc.getString("referenceNo") ?: ""
            val notes = doc.getString("notes") ?: ""
            val aepsCash = doc.getDouble("aepsCashGiven") ?: 0.0
            val dateStr = doc.getString("dateString") ?: ""
            val timestamp = doc.getTimestamp("createdAt")?.toDate()?.time ?: System.currentTimeMillis()

            TransactionEntity(
                id = 0, // Auto-increment ID in local Room
                type = typeStr,
                category = category,
                amount = amount,
                paymentMode = paymentMode,
                customerName = customerName,
                customerPhone = customerPhone,
                referenceNo = referenceNo,
                notes = notes,
                aepsCashGiven = aepsCash,
                timestamp = timestamp,
                dateString = dateStr
            )
        }
    }
}
