package com.example.data.model

data class CscCategory(
    val id: String,
    val defaultName: String,
    val isExpense: Boolean = false,
    val iconName: String = "category"
) {
    companion object {
        // Income / Service Categories
        val AADHAAR_UPDATE = CscCategory("aadhaar", "Aadhaar Update", false, "badge")
        val PAN_CARD = CscCategory("pan", "PAN Card", false, "credit_card")
        val AYUSHMAN_CARD = CscCategory("ayushman", "Ayushman Card", false, "health_and_safety")
        val PM_KISAN = CscCategory("pm_kisan", "PM Kisan", false, "agriculture")
        val AEPS_WITHDRAWAL = CscCategory("aeps", "AEPS Cash Withdrawal", false, "atm")
        val BILL_PAYMENTS = CscCategory("bill_pay", "Bill Payments", false, "receipt_long")
        val ONLINE_FORMS = CscCategory("online_form", "Online Forms / Gov Schemes", false, "description")
        val XEROX_PRINTING = CscCategory("xerox", "Xerox / Printing", false, "print")
        val PASSPORT_SEVA = CscCategory("passport", "Passport Seva", false, "flight")
        val RATION_CARD = CscCategory("ration", "Ration Card", false, "shopping_bag")
        val OTHER_INCOME = CscCategory("other_income", "Other Income", false, "attach_money")

        // Expense Categories
        val PAPER_RIM = CscCategory("paper_rim", "Paper Rim", true, "inventory_2")
        val CARTRIDGE_TONER = CscCategory("cartridge_toner", "Cartridge/Toner Refill", true, "colorize")
        val ELECTRICITY = CscCategory("electricity", "Electricity", true, "bolt")
        val SHOP_RENT = CscCategory("shop_rent", "Shop Rent", true, "store")
        val TEA_SNACKS = CscCategory("tea_snacks", "Tea/Snacks", true, "local_cafe")
        val INTERNET = CscCategory("internet", "Internet / Wi-Fi", true, "wifi")
        val MAINTENANCE = CscCategory("maintenance", "Hardware Maintenance", true, "build")
        val OTHER_EXPENSE = CscCategory("other_expense", "Other Expense", true, "money_off")

        val incomeCategories: List<CscCategory> = listOf(
            AADHAAR_UPDATE,
            PAN_CARD,
            AYUSHMAN_CARD,
            PM_KISAN,
            AEPS_WITHDRAWAL,
            BILL_PAYMENTS,
            ONLINE_FORMS,
            XEROX_PRINTING,
            PASSPORT_SEVA,
            RATION_CARD,
            OTHER_INCOME
        )

        val expenseCategories: List<CscCategory> = listOf(
            PAPER_RIM,
            CARTRIDGE_TONER,
            ELECTRICITY,
            SHOP_RENT,
            TEA_SNACKS,
            INTERNET,
            MAINTENANCE,
            OTHER_EXPENSE
        )

        fun findCategory(idOrName: String, isExpense: Boolean): CscCategory {
            val list = if (isExpense) expenseCategories else incomeCategories
            return list.firstOrNull {
                it.id.equals(idOrName, ignoreCase = true) ||
                it.defaultName.equals(idOrName, ignoreCase = true)
            } ?: if (isExpense) OTHER_EXPENSE else OTHER_INCOME
        }
    }
}
