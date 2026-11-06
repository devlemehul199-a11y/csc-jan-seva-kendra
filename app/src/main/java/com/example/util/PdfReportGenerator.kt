package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.AppLanguage
import com.example.data.model.DailySummary
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.VleProfile
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.util.Locale

object PdfReportGenerator {

    private val indianCurrencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun generateDayBookPdf(
        context: Context,
        profile: VleProfile,
        dateLabel: String,
        summary: DailySummary,
        transactions: List<TransactionEntity>,
        language: AppLanguage
    ): File {
        val document = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842 // Standard A4 in points (72 dpi)

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Draw Header Banner
        paint.color = Color.rgb(15, 58, 93) // #0F3A5D CSC Navy
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 95f, paint)

        // Accent gold stripe
        paint.color = Color.rgb(230, 138, 0) // Saffron / Gold
        canvas.drawRect(0f, 95f, pageWidth.toFloat(), 100f, paint)

        // Header Title
        paint.color = Color.WHITE
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("CSC DIGITAL KHATA - DAILY DAY-BOOK", 24f, 32f, paint)

        // Subtitle / Center Name
        paint.textSize = 12f
        paint.isFakeBoldText = false
        val centerText = "${profile.centerName}  •  VLE: ${profile.vleName}  •  CSC ID: ${profile.cscId}"
        canvas.drawText(centerText, 24f, 54f, paint)

        paint.textSize = 10f
        paint.color = Color.rgb(220, 230, 242)
        canvas.drawText("Date: $dateLabel   |   Generated: ${DateUtils.formatDateTime(System.currentTimeMillis())}", 24f, 74f, paint)

        // Executive Summary Card (Rounded box)
        var currentY = 115f
        paint.color = Color.rgb(241, 245, 249) // Light slate #F1F5F9
        val summaryRect = RectF(20f, currentY, (pageWidth - 20).toFloat(), currentY + 80f)
        canvas.drawRoundRect(summaryRect, 8f, 8f, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawRoundRect(summaryRect, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        // Summary items
        val colWidth = (pageWidth - 40) / 4f

        drawSummaryItem(canvas, paint, 20f, currentY, colWidth, "TOTAL INCOME", formatCurrency(summary.totalIncome), Color.rgb(22, 101, 52))
        drawSummaryItem(canvas, paint, 20f + colWidth, currentY, colWidth, "TOTAL EXPENSE", formatCurrency(summary.totalExpense), Color.rgb(185, 28, 28))
        drawSummaryItem(canvas, paint, 20f + colWidth * 2, currentY, colWidth, "NET PROFIT", formatCurrency(summary.netBalance), Color.rgb(15, 58, 93))
        drawSummaryItem(canvas, paint, 20f + colWidth * 3, currentY, colWidth, "CASH IN HAND", formatCurrency(summary.cashInHand), Color.rgb(30, 41, 59))

        currentY += 100f

        // Table Header
        paint.color = Color.rgb(15, 58, 93)
        val tableHeaderRect = RectF(20f, currentY, (pageWidth - 20).toFloat(), currentY + 24f)
        canvas.drawRect(tableHeaderRect, paint)

        paint.color = Color.WHITE
        paint.textSize = 9.5f
        paint.isFakeBoldText = true

        val colSr = 25f
        val colTime = 55f
        val colCat = 110f
        val colCust = 280f
        val colMode = 420f
        val colAmt = 500f

        canvas.drawText("Sr.", colSr, currentY + 16f, paint)
        canvas.drawText("Time", colTime, currentY + 16f, paint)
        canvas.drawText("Service / Category", colCat, currentY + 16f, paint)
        canvas.drawText("Customer / Note", colCust, currentY + 16f, paint)
        canvas.drawText("Mode", colMode, currentY + 16f, paint)
        canvas.drawText("Amount (Rs)", colAmt, currentY + 16f, paint)

        currentY += 24f
        paint.isFakeBoldText = false

        // Rows
        val rowHeight = 22f
        var sr = 1

        for (tx in transactions) {
            // Check if page overflow
            if (currentY + rowHeight > pageHeight - 60f) {
                // Draw footer on current page
                drawPageFooter(canvas, paint, pageWidth, pageHeight, pageNumber)
                document.finishPage(page)

                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = 40f

                // Re-draw table header on subsequent pages
                paint.color = Color.rgb(15, 58, 93)
                canvas.drawRect(RectF(20f, currentY, (pageWidth - 20).toFloat(), currentY + 24f), paint)
                paint.color = Color.WHITE
                paint.textSize = 9.5f
                paint.isFakeBoldText = true
                canvas.drawText("Sr.", colSr, currentY + 16f, paint)
                canvas.drawText("Time", colTime, currentY + 16f, paint)
                canvas.drawText("Service / Category", colCat, currentY + 16f, paint)
                canvas.drawText("Customer / Note", colCust, currentY + 16f, paint)
                canvas.drawText("Mode", colMode, currentY + 16f, paint)
                canvas.drawText("Amount (Rs)", colAmt, currentY + 16f, paint)
                currentY += 24f
                paint.isFakeBoldText = false
            }

            // Row zebra shading
            if (sr % 2 == 0) {
                paint.color = Color.rgb(248, 250, 252)
                canvas.drawRect(RectF(20f, currentY, (pageWidth - 20).toFloat(), currentY + rowHeight), paint)
            }

            paint.color = Color.rgb(51, 65, 85)
            paint.textSize = 9f

            canvas.drawText("$sr", colSr, currentY + 15f, paint)
            canvas.drawText(DateUtils.formatTime(tx.timestamp), colTime, currentY + 15f, paint)

            val catDisplay = Localization.categoryName(tx.category, tx.category, language)
            val truncatedCat = if (catDisplay.length > 28) catDisplay.take(26) + ".." else catDisplay
            canvas.drawText(truncatedCat, colCat, currentY + 15f, paint)

            val custDisplay = when {
                tx.customerName.isNotBlank() && tx.referenceNo.isNotBlank() -> "${tx.customerName} (${tx.referenceNo})"
                tx.customerName.isNotBlank() -> tx.customerName
                tx.referenceNo.isNotBlank() -> "Ref: ${tx.referenceNo}"
                tx.notes.isNotBlank() -> tx.notes
                else -> "-"
            }
            val truncatedCust = if (custDisplay.length > 24) custDisplay.take(22) + ".." else custDisplay
            canvas.drawText(truncatedCust, colCust, currentY + 15f, paint)

            canvas.drawText(tx.mode.name.replace("_", " "), colMode, currentY + 15f, paint)

            // Amount with green / red indicator
            val isIncome = tx.transactionType == TransactionType.INCOME
            paint.color = if (isIncome) Color.rgb(22, 101, 52) else Color.rgb(185, 28, 28)
            paint.isFakeBoldText = true
            val sign = if (isIncome) "+" else "-"
            canvas.drawText("$sign Rs ${formatAmount(tx.amount)}", colAmt, currentY + 15f, paint)
            paint.isFakeBoldText = false

            // Bottom border
            paint.color = Color.rgb(226, 232, 240)
            paint.strokeWidth = 0.5f
            canvas.drawLine(20f, currentY + rowHeight, (pageWidth - 20).toFloat(), currentY + rowHeight, paint)

            currentY += rowHeight
            sr++
        }

        // Draw final page footer
        drawPageFooter(canvas, paint, pageWidth, pageHeight, pageNumber)
        document.finishPage(page)

        // Write to cache file
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val cleanDate = dateLabel.replace(" ", "_").replace(",", "")
        val outputFile = File(reportsDir, "CSC_DayBook_${cleanDate}_${System.currentTimeMillis()}.pdf")
        FileOutputStream(outputFile).use { fos ->
            document.writeTo(fos)
        }
        document.close()

        return outputFile
    }

    private fun drawSummaryItem(
        canvas: Canvas,
        paint: Paint,
        startX: Float,
        startY: Float,
        width: Float,
        title: String,
        value: String,
        color: Int
    ) {
        paint.color = Color.rgb(100, 116, 139)
        paint.textSize = 7.5f
        paint.isFakeBoldText = true
        canvas.drawText(title, startX + 12f, startY + 28f, paint)

        paint.color = color
        paint.textSize = 13f
        paint.isFakeBoldText = true
        canvas.drawText(value, startX + 12f, startY + 54f, paint)
        paint.isFakeBoldText = false
    }

    private fun drawPageFooter(canvas: Canvas, paint: Paint, width: Int, height: Int, pageNumber: Int) {
        val y = height - 30f
        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = 8f
        paint.isFakeBoldText = false
        canvas.drawText("CSC Digital Khata  •  Authorized Jan Seva Kendra Daily Day-Book  •  Page $pageNumber", 24f, y, paint)
        canvas.drawText("VLE Stamp & Signature: __________________", (width - 240).toFloat(), y, paint)
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            val formatted = indianCurrencyFormat.format(amount)
            if (formatted.startsWith("₹") || formatted.startsWith("Rs.")) formatted else "₹ $formatted"
        } catch (e: Exception) {
            "₹ ${String.format(Locale.ENGLISH, "%.2f", amount)}"
        }
    }

    private fun formatAmount(amount: Double): String {
        return if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            String.format(Locale.ENGLISH, "%.2f", amount)
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "CSC Day-Book Report")
            putExtra(Intent.EXTRA_TEXT, "CSC Jan Seva Kendra Daily Day-Book Summary PDF Report.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(intent, "Share CSC Day-Book PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
