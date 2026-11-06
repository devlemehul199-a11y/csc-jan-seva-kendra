package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

object CsvBackupHelper {

    private const val CSV_HEADER = "ID,Date,Time,Type,Category,Amount,PaymentMode,CustomerName,Phone,ReferenceNo,AEPSCashGiven,Notes"

    fun exportToCsvString(transactions: List<TransactionEntity>): String {
        val sb = StringBuilder()
        sb.append(CSV_HEADER).append("\n")

        for (tx in transactions) {
            val row = listOf(
                tx.id.toString(),
                tx.dateString.ifBlank { DateUtils.toDateString(tx.timestamp) },
                DateUtils.formatTime(tx.timestamp),
                tx.type,
                escapeCsv(tx.category),
                tx.amount.toString(),
                tx.paymentMode,
                escapeCsv(tx.customerName),
                escapeCsv(tx.customerPhone),
                escapeCsv(tx.referenceNo),
                tx.aepsCashGiven.toString(),
                escapeCsv(tx.notes)
            )
            sb.append(row.joinToString(",")).append("\n")
        }
        return sb.toString()
    }

    fun exportToCsvFile(context: Context, transactions: List<TransactionEntity>): File {
        val backupsDir = File(context.cacheDir, "backups").apply { mkdirs() }
        val file = File(backupsDir, "CSC_Khata_Backup_${DateUtils.getCurrentDateString()}_${System.currentTimeMillis()}.csv")
        FileOutputStream(file).use { fos ->
            fos.write(exportToCsvString(transactions).toByteArray(Charsets.UTF_8))
        }
        return file
    }

    fun writeToOutputStream(outputStream: OutputStream, transactions: List<TransactionEntity>) {
        outputStream.use { os ->
            os.write(exportToCsvString(transactions).toByteArray(Charsets.UTF_8))
        }
    }

    fun importFromCsv(inputStream: InputStream): List<TransactionEntity> {
        val list = mutableListOf<TransactionEntity>()
        BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8)).use { reader ->
            var line = reader.readLine() // Read header
            while (reader.readLine().also { line = it } != null) {
                val current = line ?: continue
                if (current.isBlank()) continue
                val tokens = parseCsvLine(current)
                if (tokens.size >= 6) {
                    val typeStr = tokens.getOrNull(3)?.trim()?.uppercase() ?: "INCOME"
                    val validCategory = tokens.getOrNull(4)?.trim() ?: "Other"
                    val amountVal = tokens.getOrNull(5)?.toDoubleOrNull() ?: 0.0
                    val modeStr = tokens.getOrNull(6)?.trim()?.uppercase() ?: "CASH"
                    val custName = tokens.getOrNull(7) ?: ""
                    val custPhone = tokens.getOrNull(8) ?: ""
                    val refNo = tokens.getOrNull(9) ?: ""
                    val aepsGiven = tokens.getOrNull(10)?.toDoubleOrNull() ?: 0.0
                    val notesVal = tokens.getOrNull(11) ?: ""

                    val dateStr = tokens.getOrNull(1)?.trim() ?: DateUtils.getCurrentDateString()

                    val entity = TransactionEntity(
                        id = 0, // Auto-generate new primary key
                        type = if (typeStr == "EXPENSE") "EXPENSE" else "INCOME",
                        category = validCategory,
                        amount = amountVal,
                        paymentMode = when (modeStr) {
                            "ONLINE_UPI", "UPI" -> "ONLINE_UPI"
                            "BANK_TRANSFER", "BANK" -> "BANK_TRANSFER"
                            else -> "CASH"
                        },
                        customerName = custName,
                        customerPhone = custPhone,
                        referenceNo = refNo,
                        notes = notesVal,
                        aepsCashGiven = aepsGiven,
                        timestamp = System.currentTimeMillis(),
                        dateString = dateStr
                    )
                    list.add(entity)
                }
            }
        }
        return list
    }

    fun shareCsv(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "CSC Digital Khata Backup")
            putExtra(Intent.EXTRA_TEXT, "CSC Jan Seva Kendra Digital Khata Data Backup (CSV format).")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share CSC Khata CSV Backup")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val tokens = mutableListOf<String>()
        val sb = StringBuilder()
        var insideQuotes = false

        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c == '\"' -> {
                    if (insideQuotes && i + 1 < line.length && line[i + 1] == '\"') {
                        sb.append('\"')
                        i++ // Skip escaped quote
                    } else {
                        insideQuotes = !insideQuotes
                    }
                }
                c == ',' && !insideQuotes -> {
                    tokens.add(sb.toString().trim())
                    sb.clear()
                }
                else -> sb.append(c)
            }
            i++
        }
        tokens.add(sb.toString().trim())
        return tokens
    }
}
