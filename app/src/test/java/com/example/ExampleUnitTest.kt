package com.example

import com.example.data.model.AppLanguage
import com.example.data.model.CscCategory
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.util.CsvBackupHelper
import com.example.util.Localization
import com.example.util.SpeechParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream

class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun speechParser_parsesEnglishVoiceCommand() {
        val input = "Aadhaar update 100 cash Ramesh Patel"
        val parsed = SpeechParser.parse(input)

        assertEquals(100.0, parsed.amount)
        assertEquals(TransactionType.INCOME, parsed.type)
        assertEquals(CscCategory.AADHAAR_UPDATE.id, parsed.category.id)
        assertEquals(PaymentMode.CASH, parsed.paymentMode)
    }

    @Test
    fun speechParser_parsesGujaratiVoiceCommand() {
        val input = "કાગળ રીમ ૨૮૦ ખર્ચ રોકડ"
        val parsed = SpeechParser.parse(input)

        assertEquals(280.0, parsed.amount)
        assertEquals(TransactionType.EXPENSE, parsed.type)
        assertEquals(CscCategory.PAPER_RIM.id, parsed.category.id)
        assertEquals(PaymentMode.CASH, parsed.paymentMode)
    }

    @Test
    fun speechParser_parsesHindiVoiceCommand() {
        val input = "पैन कार्ड 150 ऑनलाइन सुरेश"
        val parsed = SpeechParser.parse(input)

        assertEquals(150.0, parsed.amount)
        assertEquals(TransactionType.INCOME, parsed.type)
        assertEquals(CscCategory.PAN_CARD.id, parsed.category.id)
        assertEquals(PaymentMode.ONLINE_UPI, parsed.paymentMode)
    }

    @Test
    fun csvExportAndImport_roundtripWorks() {
        val originalList = listOf(
            TransactionEntity(
                id = 1,
                type = "INCOME",
                category = "aadhaar",
                amount = 100.0,
                paymentMode = "CASH",
                customerName = "Ramesh",
                customerPhone = "9876543210",
                referenceNo = "REF-101",
                notes = "Test Note",
                dateString = "2026-11-05"
            ),
            TransactionEntity(
                id = 2,
                type = "EXPENSE",
                category = "paper_rim",
                amount = 280.0,
                paymentMode = "ONLINE_UPI",
                customerName = "Vendor",
                dateString = "2026-11-05"
            )
        )

        val csvString = CsvBackupHelper.exportToCsvString(originalList)
        assertTrue(csvString.contains("ID,Date,Time,Type,Category,Amount"))
        assertTrue(csvString.contains("aadhaar"))
        assertTrue(csvString.contains("paper_rim"))

        val inputStream = ByteArrayInputStream(csvString.toByteArray(Charsets.UTF_8))
        val imported = CsvBackupHelper.importFromCsv(inputStream)

        assertEquals(2, imported.size)
        assertEquals("INCOME", imported[0].type)
        assertEquals(100.0, imported[0].amount, 0.01)
        assertEquals("EXPENSE", imported[1].type)
        assertEquals(280.0, imported[1].amount, 0.01)
    }

    @Test
    fun localization_providesAllThreeLanguages() {
        val en = Localization.appTitle(AppLanguage.ENGLISH)
        val gu = Localization.appTitle(AppLanguage.GUJARATI)
        val hi = Localization.appTitle(AppLanguage.HINDI)

        assertEquals("CSC Digital Khata", en)
        assertEquals("સીએસસી ડિજિટલ ખાતા", gu)
        assertEquals("सीएससी डिजिटल खाता", hi)
    }

    @Test
    fun roomEntities_instantiationAndProperties() {
        val tx = com.example.data.entity.Transaction(
            id = 10,
            type = "INCOME",
            category = "aadhaar",
            amount = 120.0,
            paymentMode = "CASH",
            customerName = "Prakash"
        )
        assertEquals(10L, tx.id)
        assertEquals(TransactionType.INCOME, tx.transactionType)
        assertEquals(PaymentMode.CASH, tx.mode)

        val service = com.example.data.entity.ServiceCategory(
            id = "pan",
            name = "PAN Card Service",
            defaultFee = 150.0,
            commissionRate = 25.0,
            iconName = "credit_card"
        )
        assertEquals("pan", service.id)
        assertEquals("PAN Card Service", service.name)
        assertEquals(150.0, service.defaultFee, 0.01)

        val expense = com.example.data.entity.ExpenseCategory(
            id = "paper_rim",
            name = "Paper Rim A4",
            defaultCost = 280.0,
            iconName = "inventory_2"
        )
        assertEquals("paper_rim", expense.id)
        assertEquals("Paper Rim A4", expense.name)
        assertEquals(280.0, expense.defaultCost, 0.01)
    }
}
