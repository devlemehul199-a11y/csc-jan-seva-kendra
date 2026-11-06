package com.example.util

import com.example.data.model.CscCategory
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionType

data class ParsedVoiceResult(
    val amount: Double?,
    val type: TransactionType,
    val category: CscCategory,
    val paymentMode: PaymentMode,
    val customerName: String,
    val originalSpokenText: String
)

object SpeechParser {

    fun parse(text: String): ParsedVoiceResult {
        val normalized = normalizeDigits(text.trim())
        val lower = normalized.lowercase()

        // 1. Detect Amount
        val amount = extractAmount(lower)

        // 2. Detect Payment Mode
        val mode = when {
            containsAny(lower, "upi", "gpay", "google pay", "phonepe", "paytm", "online", "ઓનલાઇન", "ऑनलाइन", "યુપીઆઈ", "यूपीआई") ->
                PaymentMode.ONLINE_UPI
            containsAny(lower, "bank", "neft", "rtgs", "બેંક", "बैंक", "transfer") ->
                PaymentMode.BANK_TRANSFER
            containsAny(lower, "cash", "rokad", "rokar", "રોકડ", "नकद", "રોકડિયા") ->
                PaymentMode.CASH
            else -> PaymentMode.CASH
        }

        // 3. Detect Category
        val category = detectCategory(lower)

        // 4. Detect Type
        val hasExpenseKeyword = containsAny(
            lower,
            "kharch", "kharcha", "ખર્ચ", "ખર્ચા", "ખરીદી", "kharidi", "expense", "paid",
            "chuko", "vyay", "આપ્યા", "દીધા", "खर्च"
        )
        val hasIncomeKeyword = containsAny(
            lower,
            "aavak", "aavya", "malya", "આવક", "મળ્યા", "જમા", "jama", "income", "received", "kamani"
        )

        val type = when {
            hasExpenseKeyword -> TransactionType.EXPENSE
            hasIncomeKeyword -> TransactionType.INCOME
            category.isExpense -> TransactionType.EXPENSE
            else -> TransactionType.INCOME
        }

        // 5. Extract Customer Name (if any names mentioned)
        val customerName = extractCustomerName(normalized)

        return ParsedVoiceResult(
            amount = amount,
            type = type,
            category = category,
            paymentMode = mode,
            customerName = customerName,
            originalSpokenText = text
        )
    }

    private fun normalizeDigits(input: String): String {
        val gujaratiDigits = "૦૧૨૩૪૫૬૭૮૯"
        val devanagariDigits = "०१२३४५६७८९"
        val asciiDigits = "0123456789"

        var result = input
        for (i in 0..9) {
            result = result.replace(gujaratiDigits[i], asciiDigits[i])
            result = result.replace(devanagariDigits[i], asciiDigits[i])
        }
        return result
    }

    private fun extractAmount(text: String): Double? {
        val numberRegex = Regex("""(?<!\w)(\d+(?:\.\d+)?)(?!\w)""")
        val matches = numberRegex.findAll(text).toList()
        if (matches.isNotEmpty()) {
            return matches.first().value.toDoubleOrNull()
        }

        // Check word-based amounts
        if (text.contains("hazar") || text.contains("હજાર") || text.contains("हज़ार")) return 1000.0
        if (text.contains("sau") || text.contains("સો") || text.contains("सौ")) return 100.0
        if (text.contains("pachas") || text.contains("પચાસ") || text.contains("पचास")) return 50.0

        return null
    }

    private fun detectCategory(lower: String): CscCategory {
        return when {
            containsAny(lower, "aadhaar", "adhar", "આધાર", "आधार", "fingerprint", "biometric") -> CscCategory.AADHAAR_UPDATE
            containsAny(lower, "pan", "પાન", "पैन", "pan card") -> CscCategory.PAN_CARD
            containsAny(lower, "ayushman", "pmjay", "golden card", "આયુષ્માન", "आयुष्मान") -> CscCategory.AYUSHMAN_CARD
            containsAny(lower, "pm kisan", "kisan", "કિસાન", "किसान", "ekyc") -> CscCategory.PM_KISAN
            containsAny(lower, "aeps", "digipay", "રોકડ ઉપાડ", "ઉપાડ", "withdrawal", "निकासी") -> CscCategory.AEPS_WITHDRAWAL
            containsAny(lower, "light bill", "bijli", "electricity", "વીજળી", "લાઇટ બિલ", "લાઈટ બિલ", "ટોરેન્ટ", "ugvcl", "dgvcl", "pgvcl", "mgvcl") ->
                if (containsAny(lower, "shop", "office", "দુકਾਨ", "દુકાન")) CscCategory.ELECTRICITY else CscCategory.BILL_PAYMENTS
            containsAny(lower, "bill", "recharge", "બિલ", "રિચાર્જ") -> CscCategory.BILL_PAYMENTS
            containsAny(lower, "xerox", "print", "photocopy", "કલર પ્રિન્ટ", "ઝેરોક્ષ", "પ્રિન્ટ", "फोटोकॉपी") -> CscCategory.XEROX_PRINTING
            containsAny(lower, "paper", "rim", "kagaj", "કાગળ", "कागज़", "રીમ", "પેપર") -> CscCategory.PAPER_RIM
            containsAny(lower, "toner", "cartridge", "refill", "ઇંક", "શાહી", "ટોનર", "रिफिल") -> CscCategory.CARTRIDGE_TONER
            containsAny(lower, "rent", "bhadu", "ભાડું", "ભાડા", "किराया") -> CscCategory.SHOP_RENT
            containsAny(lower, "tea", "chai", "snack", "nasto", "biscuit", "ચા", "નાસ્તો", "નાસ્તો", "ચાય") -> CscCategory.TEA_SNACKS
            containsAny(lower, "wifi", "internet", "broadband", "નેટ", "ઇન્ટરનેટ") -> CscCategory.INTERNET
            containsAny(lower, "passport", "પાસપોર્ટ", "पासपोर्ट") -> CscCategory.PASSPORT_SEVA
            containsAny(lower, "ration", "રાશન", "રેશન", "राशन") -> CscCategory.RATION_CARD
            containsAny(lower, "form", "yojana", "online", "યોજના", "સરકારી") -> CscCategory.ONLINE_FORMS
            else -> CscCategory.OTHER_INCOME
        }
    }

    private fun extractCustomerName(text: String): String {
        // Strip out common tokens to see if a person's name was said
        val stopWords = listOf(
            "aadhaar", "pan", "ayushman", "kisan", "aeps", "bill", "xerox", "print",
            "paper", "rim", "tea", "chai", "cash", "rokad", "online", "upi", "bank",
            "rupiya", "rupees", "rs", "kharch", "income", "aavak", "rupya", "rupia",
            "આધાર", "પાન", "રોકડ", "ઓનલાઇન", "રૂપિયા", "ખર્ચ", "આવક", "રમેશ", "નકદ",
            "कागज़", "रुपये", "पैन", "आधार"
        )
        val tokens = text.split(Regex("""[\s,]+""")).filter { token ->
            val clean = token.lowercase().replace(Regex("[^a-zA-Z\\u0A80-\\u0AFF\\u0900-\\u097F]"), "")
            clean.length > 2 && !clean.all { it.isDigit() } && !stopWords.any { clean.contains(it) }
        }
        return tokens.take(2).joinToString(" ").trim()
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
