package com.example.util

import com.example.data.model.AppLanguage
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionType

object Localization {

    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "સીએસસી ડિજિટલ ખાતા"
        AppLanguage.HINDI -> "सीएससी डिजिटल खाता"
        AppLanguage.ENGLISH -> "CSC Digital Khata"
    }

    fun appSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "જન સેવા કેન્દ્ર દૈનિક રોજમેળ"
        AppLanguage.HINDI -> "जन सेवा केंद्र दैनिक रोजमेल"
        AppLanguage.ENGLISH -> "Jan Seva Kendra Day-Book"
    }

    fun cashInHand(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "હાથ પર રોકડ"
        AppLanguage.HINDI -> "हाथ में नकद"
        AppLanguage.ENGLISH -> "Cash in Hand"
    }

    fun bankBalance(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "બેંક / UPI સિલક"
        AppLanguage.HINDI -> "बैंक / UPI शेष"
        AppLanguage.ENGLISH -> "Bank / UPI Balance"
    }

    fun todayIncome(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આજની આવક"
        AppLanguage.HINDI -> "आज की आय"
        AppLanguage.ENGLISH -> "Today's Income"
    }

    fun todayExpense(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આજનો ખર્ચ"
        AppLanguage.HINDI -> "आज का खर्च"
        AppLanguage.ENGLISH -> "Today's Expense"
    }

    fun netBalance(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ચોખ્ખો નફો / સિલક"
        AppLanguage.HINDI -> "शुद्ध लाभ / शेष"
        AppLanguage.ENGLISH -> "Net Profit / Balance"
    }

    fun addIncome(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આવક ઉમેરો"
        AppLanguage.HINDI -> "आय जोड़ें"
        AppLanguage.ENGLISH -> "Add Income"
    }

    fun addExpense(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ખર્ચ ઉમેરો"
        AppLanguage.HINDI -> "खर्च जोड़ें"
        AppLanguage.ENGLISH -> "Add Expense"
    }

    fun voiceEntry(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "બોલીને એન્ટ્રી"
        AppLanguage.HINDI -> "बोलकर एंट्री"
        AppLanguage.ENGLISH -> "Voice Entry"
    }

    fun filterToday(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આજે"
        AppLanguage.HINDI -> "आज"
        AppLanguage.ENGLISH -> "Today"
    }

    fun filterYesterday(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ગઈકાલે"
        AppLanguage.HINDI -> "कल"
        AppLanguage.ENGLISH -> "Yesterday"
    }

    fun filterThisWeek(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આ અઠવાડિયે"
        AppLanguage.HINDI -> "इस सप्ताह"
        AppLanguage.ENGLISH -> "This Week"
    }

    fun filterThisMonth(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આ મહિને"
        AppLanguage.HINDI -> "इस महीने"
        AppLanguage.ENGLISH -> "This Month"
    }

    fun filterAll(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "બધા વ્યવહારો"
        AppLanguage.HINDI -> "सभी लेन-देन"
        AppLanguage.ENGLISH -> "All Records"
    }

    fun amountLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "રકમ (₹)"
        AppLanguage.HINDI -> "राशि (₹)"
        AppLanguage.ENGLISH -> "Amount (₹)"
    }

    fun categoryLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "સેવા / કેટેગરી"
        AppLanguage.HINDI -> "सेवा / श्रेणी"
        AppLanguage.ENGLISH -> "Service / Category"
    }

    fun paymentModeLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ચૂકવણી પદ્ધતિ"
        AppLanguage.HINDI -> "भुगतान प्रकार"
        AppLanguage.ENGLISH -> "Payment Mode"
    }

    fun customerLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ગ્રાહક / લાભાર્થીનું નામ"
        AppLanguage.HINDI -> "ग्राहक / लाभार्थी का नाम"
        AppLanguage.ENGLISH -> "Customer / Beneficiary"
    }

    fun phoneLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "મોબાઇલ નંબર (વૈકલ્પિક)"
        AppLanguage.HINDI -> "मोबाइल नंबर (वैकल्पिक)"
        AppLanguage.ENGLISH -> "Mobile Number (Optional)"
    }

    fun refLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "રસીદ / એકનોલેજમેન્ટ નં."
        AppLanguage.HINDI -> "रसीद / पावती संख्या"
        AppLanguage.ENGLISH -> "Receipt / Ref No."
    }

    fun notesLabel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "નોંધ / વિગત"
        AppLanguage.HINDI -> "टिप्पणी / विवरण"
        AppLanguage.ENGLISH -> "Notes / Remarks"
    }

    fun save(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "એન્ટ્રી સાચવો"
        AppLanguage.HINDI -> "एंट्री सहेजें"
        AppLanguage.ENGLISH -> "Save Entry"
    }

    fun cancel(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "રદ કરો"
        AppLanguage.HINDI -> "रद्द करें"
        AppLanguage.ENGLISH -> "Cancel"
    }

    fun delete(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "કાઢી નાખો"
        AppLanguage.HINDI -> "हटाएं"
        AppLanguage.ENGLISH -> "Delete"
    }

    fun edit(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ફેરફાર કરો"
        AppLanguage.HINDI -> "संपादित करें"
        AppLanguage.ENGLISH -> "Edit"
    }

    fun searchPlaceholder(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "ગ્રાહક, સેવા કે નોંધ શોધો..."
        AppLanguage.HINDI -> "ग्राहक, सेवा या टिप्पणी खोजें..."
        AppLanguage.ENGLISH -> "Search customer, service or note..."
    }

    fun noTransactions(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "આ સમયગાળામાં કોઈ વ્યવહાર નથી"
        AppLanguage.HINDI -> "इस अवधि में कोई लेन-देन नहीं है"
        AppLanguage.ENGLISH -> "No transactions for this period"
    }

    fun exportPdf(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "PDF રિપોર્ટ કાઢો"
        AppLanguage.HINDI -> "PDF रिपोर्ट निकालें"
        AppLanguage.ENGLISH -> "Generate PDF Day-Book"
    }

    fun shareReport(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "WhatsApp / પ્રિન્ટર પર શેર કરો"
        AppLanguage.HINDI -> "WhatsApp / प्रिंटर पर शेयर करें"
        AppLanguage.ENGLISH -> "Share via WhatsApp / Print"
    }

    fun exportCsv(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "Excel (CSV) બેકઅપ બનાવો"
        AppLanguage.HINDI -> "Excel (CSV) बैकअप बनाएं"
        AppLanguage.ENGLISH -> "Export Excel (CSV) Backup"
    }

    fun restoreCsv(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "CSV બેકઅપ રિસ્ટોર કરો"
        AppLanguage.HINDI -> "CSV बैकअप रीस्टोर करें"
        AppLanguage.ENGLISH -> "Restore from CSV"
    }

    fun voicePrompt(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "બોલો: 'આધાર અપડેટ ૧૦૦ રોકડ રમેશભાઈ' અથવા 'કાગળ રીમ ૨૮૦ ખર્ચ'"
        AppLanguage.HINDI -> "बोलें: 'पैन कार्ड १५૦ नकद' या 'कागज़ रिम २८० खर्च'"
        AppLanguage.ENGLISH -> "Speak: 'Aadhaar update 100 cash' or 'Paper rim 280 expense'"
    }

    fun listening(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "સાંભળી રહ્યું છે... બોલો"
        AppLanguage.HINDI -> "सुन रहा है... बोलिए"
        AppLanguage.ENGLISH -> "Listening... Please speak"
    }

    fun aepsCashWithdrawalNotice(lang: AppLanguage): String = when (lang) {
        AppLanguage.GUJARATI -> "AEPS: ગ્રાહકને રોકડ આપી, રકમ + કમિશન બેંકમાં જમા થયા"
        AppLanguage.HINDI -> "AEPS: ग्राहक को नकद दिया, राशि + कमीशन बैंक में जमा"
        AppLanguage.ENGLISH -> "AEPS: Cash handed to client, amount + comm. in Bank"
    }

    fun paymentModeName(mode: PaymentMode, lang: AppLanguage): String = when (mode) {
        PaymentMode.CASH -> when (lang) {
            AppLanguage.GUJARATI -> "રોકડ (Cash)"
            AppLanguage.HINDI -> "नकद (Cash)"
            AppLanguage.ENGLISH -> "Cash"
        }
        PaymentMode.ONLINE_UPI -> when (lang) {
            AppLanguage.GUJARATI -> "ઓનલાઇન (UPI)"
            AppLanguage.HINDI -> "ऑनलाइन (UPI)"
            AppLanguage.ENGLISH -> "Online UPI"
        }
        PaymentMode.BANK_TRANSFER -> when (lang) {
            AppLanguage.GUJARATI -> "બેંક ટ્રાન્સફર"
            AppLanguage.HINDI -> "बैंक ट्रांसफर"
            AppLanguage.ENGLISH -> "Bank Transfer"
        }
    }

    fun transactionTypeName(type: TransactionType, lang: AppLanguage): String = when (type) {
        TransactionType.INCOME -> when (lang) {
            AppLanguage.GUJARATI -> "આવક (Income)"
            AppLanguage.HINDI -> "आय / जमा"
            AppLanguage.ENGLISH -> "Income"
        }
        TransactionType.EXPENSE -> when (lang) {
            AppLanguage.GUJARATI -> "ખર્ચ (Expense)"
            AppLanguage.HINDI -> "खर्च (Expense)"
            AppLanguage.ENGLISH -> "Expense"
        }
    }

    fun categoryName(categoryId: String, defaultName: String, lang: AppLanguage): String {
        return when (categoryId.lowercase()) {
            "aadhaar" -> when (lang) {
                AppLanguage.GUJARATI -> "આધાર કાર્ડ અપડેટ"
                AppLanguage.HINDI -> "आधार कार्ड अपडेट"
                AppLanguage.ENGLISH -> "Aadhaar Update"
            }
            "pan" -> when (lang) {
                AppLanguage.GUJARATI -> "પાન કાર્ડ સેવા"
                AppLanguage.HINDI -> "पैन कार्ड सेवा"
                AppLanguage.ENGLISH -> "PAN Card"
            }
            "ayushman" -> when (lang) {
                AppLanguage.GUJARATI -> "આયુષ્માન ભારત કાર્ડ"
                AppLanguage.HINDI -> "आयुष्मान भारत कार्ड"
                AppLanguage.ENGLISH -> "Ayushman Card"
            }
            "pm_kisan" -> when (lang) {
                AppLanguage.GUJARATI -> "પીએમ કિસાન કેવાયસી"
                AppLanguage.HINDI -> "पीएम किसान ई-केवाईसी"
                AppLanguage.ENGLISH -> "PM Kisan KYC"
            }
            "aeps" -> when (lang) {
                AppLanguage.GUJARATI -> "AEPS રોકડ ઉપાડ"
                AppLanguage.HINDI -> "AEPS नकद निकासी"
                AppLanguage.ENGLISH -> "AEPS Withdrawal"
            }
            "bill_pay" -> when (lang) {
                AppLanguage.GUJARATI -> "લાઇટ બિલ / બિલ પેમેન્ટ"
                AppLanguage.HINDI -> "बिजली व अन्य बिल भुगतान"
                AppLanguage.ENGLISH -> "Bill Payments"
            }
            "online_form" -> when (lang) {
                AppLanguage.GUJARATI -> "ઓનલાઇન સરકારી ફોર્મ"
                AppLanguage.HINDI -> "ऑनलाइन सरकारी फॉर्म"
                AppLanguage.ENGLISH -> "Online Gov Forms"
            }
            "xerox" -> when (lang) {
                AppLanguage.GUJARATI -> "ઝેરોક્ષ / પ્રિન્ટિંગ"
                AppLanguage.HINDI -> "फोटोकॉपी / प्रिंट"
                AppLanguage.ENGLISH -> "Xerox / Printing"
            }
            "passport" -> when (lang) {
                AppLanguage.GUJARATI -> "પાસપોર્ટ સેવા"
                AppLanguage.HINDI -> "पासपोर्ट सेवा"
                AppLanguage.ENGLISH -> "Passport Seva"
            }
            "ration" -> when (lang) {
                AppLanguage.GUJARATI -> "રેશન કાર્ડ સેવા"
                AppLanguage.HINDI -> "राशन कार्ड सेवा"
                AppLanguage.ENGLISH -> "Ration Card"
            }
            "paper_rim" -> when (lang) {
                AppLanguage.GUJARATI -> "કાગળ રીમ (A4/Legal)"
                AppLanguage.HINDI -> "कागज़ रिम (A4/Legal)"
                AppLanguage.ENGLISH -> "Paper Rim"
            }
            "cartridge_toner" -> when (lang) {
                AppLanguage.GUJARATI -> "ટોનર / કાર્ટ્રિજ રિફિલ"
                AppLanguage.HINDI -> "टोनर / कार्ट्रिज रिफिल"
                AppLanguage.ENGLISH -> "Cartridge/Toner Refill"
            }
            "electricity" -> when (lang) {
                AppLanguage.GUJARATI -> "દુકાન લાઇટ બિલ"
                AppLanguage.HINDI -> "दुकान बिजली बिल"
                AppLanguage.ENGLISH -> "Electricity"
            }
            "shop_rent" -> when (lang) {
                AppLanguage.GUJARATI -> "દુકાન ભાડું"
                AppLanguage.HINDI -> "दुकान किराया"
                AppLanguage.ENGLISH -> "Shop Rent"
            }
            "tea_snacks" -> when (lang) {
                AppLanguage.GUJARATI -> "ચા - નાસ્તો - પાણી"
                AppLanguage.HINDI -> "चाय - नाश्ता - पानी"
                AppLanguage.ENGLISH -> "Tea / Snacks"
            }
            "internet" -> when (lang) {
                AppLanguage.GUJARATI -> "ઇન્ટરનેટ / બ્રોડબેન્ડ"
                AppLanguage.HINDI -> "इंटरनेट / ब्रॉडबैंड"
                AppLanguage.ENGLISH -> "Internet / Wi-Fi"
            }
            "maintenance" -> when (lang) {
                AppLanguage.GUJARATI -> "પ્રિન્ટર / કોમ્પ્યુટર રીપેરીંગ"
                AppLanguage.HINDI -> "प्रिंटर / कंप्यूटर मरम्मत"
                AppLanguage.ENGLISH -> "Hardware Maintenance"
            }
            else -> defaultName
        }
    }
}
