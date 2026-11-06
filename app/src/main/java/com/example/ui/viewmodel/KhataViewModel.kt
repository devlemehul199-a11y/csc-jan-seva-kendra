package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.firestore.FirestoreRepository
import com.example.data.model.AppLanguage
import com.example.data.model.CscCategory
import com.example.data.model.DailySummary
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.VleProfile
import com.example.data.repository.TransactionRepository
import com.example.ui.components.MonthData
import com.example.util.CsvBackupHelper
import com.example.util.DateUtils
import com.example.util.ParsedVoiceResult
import com.example.util.PdfReportGenerator
import com.example.util.SpeechParser
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class DateFilter {
    TODAY,
    YESTERDAY,
    THIS_WEEK,
    THIS_MONTH,
    THIS_YEAR,
    ALL
}

enum class SyncStatus {
    IDLE,
    SYNCING,
    SUCCESS,
    ERROR
}

class KhataViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val firestoreRepository: FirestoreRepository
    private val categoryDao = AppDatabase.getDatabase(application).categoryDao()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(db.transactionDao())
        firestoreRepository = FirestoreRepository.create(application)
    }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    private val _syncMessage = MutableStateFlow("")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.GUJARATI)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _selectedFilter = MutableStateFlow(DateFilter.TODAY)
    val selectedFilter: StateFlow<DateFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTypeFilter = MutableStateFlow<TransactionType?>(null)
    val selectedTypeFilter: StateFlow<TransactionType?> = _selectedTypeFilter.asStateFlow()

    private val _vleProfile = MutableStateFlow(
        VleProfile(
            centerName = "Jan Seva Kendra - CSC Center",
            vleName = "Patel Mehul (VLE)",
            cscId = "CSC248901234",
            phone = "+91 98765 43210",
            openingCash = 5000.0,
            openingBank = 25000.0
        )
    )
    val vleProfile: StateFlow<VleProfile> = _vleProfile.asStateFlow()

    private val _voiceResult = MutableStateFlow<ParsedVoiceResult?>(null)
    val voiceResult: StateFlow<ParsedVoiceResult?> = _voiceResult.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Dialog control
    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog.asStateFlow()

    private val _editingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val editingTransaction: StateFlow<TransactionEntity?> = _editingTransaction.asStateFlow()

    private val _initialDialogType = MutableStateFlow(TransactionType.INCOME)
    val initialDialogType: StateFlow<TransactionType> = _initialDialogType.asStateFlow()

    private val _showVoiceDialog = MutableStateFlow(false)
    val showVoiceDialog: StateFlow<Boolean> = _showVoiceDialog.asStateFlow()

    private val _showAuthDialog = MutableStateFlow(false)
    val showAuthDialog: StateFlow<Boolean> = _showAuthDialog.asStateFlow()

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _selectedFilter,
        _searchQuery,
        _selectedTypeFilter
    ) { list, filter, query, typeFilter ->
        val todayStr = DateUtils.getCurrentDateString()
        val yesterdayStr = DateUtils.getYesterdayDateString()
        val startOfWeek = DateUtils.getStartOfWeek()
        val startOfMonth = DateUtils.getStartOfMonth()
        val startOfYear = DateUtils.getStartOfYear()

        list.filter { tx ->
            // Date filter
            val dateMatches = when (filter) {
                DateFilter.TODAY -> tx.dateString == todayStr || DateUtils.toDateString(tx.timestamp) == todayStr
                DateFilter.YESTERDAY -> tx.dateString == yesterdayStr || DateUtils.toDateString(tx.timestamp) == yesterdayStr
                DateFilter.THIS_WEEK -> tx.timestamp >= startOfWeek
                DateFilter.THIS_MONTH -> tx.timestamp >= startOfMonth
                DateFilter.THIS_YEAR -> tx.timestamp >= startOfYear
                DateFilter.ALL -> true
            }

            // Type filter
            val typeMatches = typeFilter == null || tx.transactionType == typeFilter

            // Query filter
            val queryMatches = if (query.isBlank()) {
                true
            } else {
                val q = query.lowercase().trim()
                tx.customerName.lowercase().contains(q) ||
                tx.category.lowercase().contains(q) ||
                tx.notes.lowercase().contains(q) ||
                tx.referenceNo.lowercase().contains(q) ||
                tx.amount.toString().contains(q)
            }

            dateMatches && typeMatches && queryMatches
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val summary: StateFlow<DailySummary> = combine(
        filteredTransactions,
        _vleProfile
    ) { list, profile ->
        var totalIncome = 0.0
        var totalExpense = 0.0
        var cashIn = profile.openingCash
        var bankIn = profile.openingBank
        var incCount = 0
        var expCount = 0
        val catMap = mutableMapOf<String, Double>()
        val modeMap = mutableMapOf<PaymentMode, Double>()

        for (tx in list) {
            val isIncome = tx.transactionType == TransactionType.INCOME
            if (isIncome) {
                totalIncome += tx.amount
                incCount++
            } else {
                totalExpense += tx.amount
                expCount++
            }

            // Category breakdown
            catMap[tx.category] = (catMap[tx.category] ?: 0.0) + tx.amount

            // Payment mode
            val mode = tx.mode
            modeMap[mode] = (modeMap[mode] ?: 0.0) + tx.amount

            // Balance calculations
            when (mode) {
                PaymentMode.CASH -> {
                    if (isIncome) {
                        cashIn += tx.amount
                    } else {
                        cashIn -= tx.amount
                    }
                    if (tx.aepsCashGiven > 0) {
                        cashIn -= tx.aepsCashGiven
                        bankIn += tx.aepsCashGiven
                    }
                }
                PaymentMode.ONLINE_UPI, PaymentMode.BANK_TRANSFER -> {
                    if (isIncome) {
                        bankIn += tx.amount
                    } else {
                        bankIn -= tx.amount
                    }
                }
            }
        }

        DailySummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            netBalance = totalIncome - totalExpense,
            cashInHand = cashIn,
            bankUpiBalance = bankIn,
            transactionCount = list.size,
            incomeCount = incCount,
            expenseCount = expCount,
            categoryTotals = catMap,
            paymentModeTotals = modeMap
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DailySummary()
    )

    // Monthly trends for analytics
    val monthlyTrends: StateFlow<List<MonthData>> = allTransactions.map { list ->
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())
        val cal = Calendar.getInstance()
        val currentYear = cal.get(Calendar.YEAR)

        // Initialize 12 months for current year
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val incMap = mutableMapOf<String, Double>()
        val expMap = mutableMapOf<String, Double>()

        monthNames.forEach { m ->
            incMap[m] = 0.0
            expMap[m] = 0.0
        }

        list.forEach { tx ->
            cal.timeInMillis = tx.timestamp
            if (cal.get(Calendar.YEAR) == currentYear) {
                val mName = monthFormat.format(Date(tx.timestamp))
                if (tx.transactionType == TransactionType.INCOME) {
                    incMap[mName] = (incMap[mName] ?: 0.0) + tx.amount
                } else {
                    expMap[mName] = (expMap[mName] ?: 0.0) + tx.amount
                }
            }
        }

        monthNames.map { m ->
            MonthData(
                monthName = m,
                income = incMap[m] ?: 0.0,
                expense = expMap[m] ?: 0.0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Observe Auth State
        Firebase.auth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
            if (auth.currentUser != null) {
                loadCloudUserProfile()
            }
        }

        // Seed sample data if database is fresh
        viewModelScope.launch {
            if (categoryDao.getServiceCategoryCount() == 0) {
                categoryDao.insertServiceCategories(
                    CscCategory.incomeCategories.map {
                        com.example.data.entity.ServiceCategory(
                            id = it.id,
                            name = it.defaultName,
                            iconName = it.iconName
                        )
                    }
                )
            }
            if (categoryDao.getExpenseCategoryCount() == 0) {
                categoryDao.insertExpenseCategories(
                    CscCategory.expenseCategories.map {
                        com.example.data.entity.ExpenseCategory(
                            id = it.id,
                            name = it.defaultName,
                            iconName = it.iconName
                        )
                    }
                )
            }

            val count = repository.getAllSnapshot().size
            if (count == 0) {
                seedInitialData()
            }
        }
    }

    private fun loadCloudUserProfile() {
        viewModelScope.launch {
            try {
                val cloudProfile = firestoreRepository.fetchUserProfile()
                if (cloudProfile != null) {
                    _vleProfile.value = cloudProfile
                }
            } catch (e: Exception) {
                Log.w("KhataViewModel", "Error fetching cloud profile: ${e.message}")
            }
        }
    }

    private suspend fun seedInitialData() {
        val todayStr = DateUtils.getCurrentDateString()
        val now = System.currentTimeMillis()
        val minute = 60 * 1000L

        val seedList = listOf(
            TransactionEntity(
                type = "INCOME",
                category = CscCategory.AADHAAR_UPDATE.id,
                amount = 100.0,
                paymentMode = "CASH",
                customerName = "Ramesh Patel",
                referenceNo = "SRN-8472910",
                notes = "Biometric & address update",
                timestamp = now - 35 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "INCOME",
                category = CscCategory.PAN_CARD.id,
                amount = 150.0,
                paymentMode = "ONLINE_UPI",
                customerName = "Mahesh Solanki",
                referenceNo = "UTI-PAN-9382",
                notes = "New PAN application online",
                timestamp = now - 95 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "INCOME",
                category = CscCategory.AEPS_WITHDRAWAL.id,
                amount = 35.0,
                aepsCashGiven = 3000.0,
                paymentMode = "CASH",
                customerName = "Geetaben Barot",
                referenceNo = "AEPS-92841",
                notes = "₹3,000 SBI Cash withdrawal",
                timestamp = now - 140 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "INCOME",
                category = CscCategory.AYUSHMAN_CARD.id,
                amount = 60.0,
                paymentMode = "CASH",
                customerName = "Kamlesh Prajapati",
                referenceNo = "PMJAY-6638",
                notes = "PVC card print & eKYC",
                timestamp = now - 210 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "EXPENSE",
                category = CscCategory.PAPER_RIM.id,
                amount = 280.0,
                paymentMode = "CASH",
                customerName = "Shreeji Stationery",
                referenceNo = "INV-441",
                notes = "JK Copier A4 75GSM 1 Rim",
                timestamp = now - 280 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "INCOME",
                category = CscCategory.XEROX_PRINTING.id,
                amount = 45.0,
                paymentMode = "ONLINE_UPI",
                customerName = "Pooja Shah",
                notes = "9 Pages color prints",
                timestamp = now - 320 * minute,
                dateString = todayStr
            ),
            TransactionEntity(
                type = "EXPENSE",
                category = CscCategory.TEA_SNACKS.id,
                amount = 40.0,
                paymentMode = "CASH",
                customerName = "Radhe Tea Stall",
                notes = "Morning tea for staff",
                timestamp = now - 380 * minute,
                dateString = todayStr
            )
        )
        repository.insertAll(seedList)
    }

    fun syncToCloud() {
        if (_currentUser.value == null) {
            _showAuthDialog.value = true
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _syncStatus.value = SyncStatus.SYNCING
            _syncMessage.value = "Backing up Day-Book records to Firestore..."
            try {
                // Save Profile first
                firestoreRepository.saveUserProfile(_vleProfile.value)

                // Sync all local transactions to cloud
                val list = repository.getAllSnapshot()
                val syncedCount = firestoreRepository.syncTransactionsToCloud(list) { current, total ->
                    _syncMessage.value = "Syncing $current of $total records..."
                }

                withContext(Dispatchers.Main) {
                    _syncStatus.value = SyncStatus.SUCCESS
                    _syncMessage.value = "Synced $syncedCount records to Cloud!"
                    _snackbarMessage.value = "Cloud Backup Complete ($syncedCount records)"
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _syncStatus.value = SyncStatus.ERROR
                    _syncMessage.value = "Sync failed: ${e.localizedMessage}"
                    _snackbarMessage.value = "Cloud sync error: ${e.localizedMessage}"
                }
            }
        }
    }

    fun restoreFromCloud() {
        if (_currentUser.value == null) {
            _showAuthDialog.value = true
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _syncStatus.value = SyncStatus.SYNCING
            _syncMessage.value = "Restoring Day-Book records from Firestore..."
            try {
                val cloudTxns = firestoreRepository.fetchTransactionsFromCloud()
                if (cloudTxns.isNotEmpty()) {
                    repository.insertAll(cloudTxns)
                    withContext(Dispatchers.Main) {
                        _syncStatus.value = SyncStatus.SUCCESS
                        _syncMessage.value = "Restored ${cloudTxns.size} records from Cloud!"
                        _snackbarMessage.value = "Restored ${cloudTxns.size} records from Cloud!"
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _syncStatus.value = SyncStatus.SUCCESS
                        _syncMessage.value = "No existing cloud records found"
                        _snackbarMessage.value = "No existing cloud records found"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _syncStatus.value = SyncStatus.ERROR
                    _syncMessage.value = "Restore failed: ${e.localizedMessage}"
                    _snackbarMessage.value = "Cloud restore error: ${e.localizedMessage}"
                }
            }
        }
    }

    fun openAuthDialog() {
        _showAuthDialog.value = true
    }

    fun closeAuthDialog() {
        _showAuthDialog.value = false
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setFilter(filter: DateFilter) {
        _selectedFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setTypeFilter(type: TransactionType?) {
        _selectedTypeFilter.value = type
    }

    fun openAddDialog(type: TransactionType = TransactionType.INCOME) {
        _editingTransaction.value = null
        _initialDialogType.value = type
        _showAddDialog.value = true
    }

    fun openEditDialog(transaction: TransactionEntity) {
        _editingTransaction.value = transaction
        _initialDialogType.value = transaction.transactionType
        _showAddDialog.value = true
    }

    fun closeAddDialog() {
        _showAddDialog.value = false
        _editingTransaction.value = null
    }

    fun openVoiceDialog() {
        _showVoiceDialog.value = true
    }

    fun closeVoiceDialog() {
        _showVoiceDialog.value = false
    }

    fun saveTransaction(
        type: TransactionType,
        category: String,
        amount: Double,
        mode: PaymentMode,
        customerName: String,
        customerPhone: String,
        referenceNo: String,
        notes: String,
        aepsCashGiven: Double = 0.0
    ) {
        viewModelScope.launch {
            val existing = _editingTransaction.value
            if (existing != null) {
                val updated = existing.copy(
                    type = type.name,
                    category = category,
                    amount = amount,
                    paymentMode = mode.name,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    referenceNo = referenceNo,
                    notes = notes,
                    aepsCashGiven = aepsCashGiven
                )
                repository.update(updated)
                _snackbarMessage.value = "Transaction updated successfully"
            } else {
                val entity = TransactionEntity(
                    type = type.name,
                    category = category,
                    amount = amount,
                    paymentMode = mode.name,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    referenceNo = referenceNo,
                    notes = notes,
                    aepsCashGiven = aepsCashGiven,
                    timestamp = System.currentTimeMillis(),
                    dateString = DateUtils.getCurrentDateString()
                )
                repository.insert(entity)
                _snackbarMessage.value = "Transaction saved to Day-Book"
            }
            closeAddDialog()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
            _snackbarMessage.value = "Transaction deleted"
        }
    }

    fun processSpokenText(spokenText: String) {
        val result = SpeechParser.parse(spokenText)
        _voiceResult.value = result
    }

    fun applyVoiceResultAndOpenAdd(result: ParsedVoiceResult) {
        _editingTransaction.value = null
        _initialDialogType.value = result.type
        _showVoiceDialog.value = false
        _showAddDialog.value = true
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun updateProfile(profile: VleProfile) {
        _vleProfile.value = profile
        _snackbarMessage.value = "Profile updated"
        // Also update cloud if signed in
        if (_currentUser.value != null) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    firestoreRepository.saveUserProfile(profile)
                } catch (e: Exception) {
                    Log.w("KhataViewModel", "Failed to update profile to cloud: ${e.message}")
                }
            }
        }
    }

    fun generateAndSharePdf(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentList = filteredTransactions.value
            val currentSumm = summary.value
            val prof = vleProfile.value
            val lang = currentLanguage.value
            val dateLabel = when (selectedFilter.value) {
                DateFilter.TODAY -> DateUtils.formatDisplayDate(DateUtils.getCurrentDateString())
                DateFilter.YESTERDAY -> DateUtils.formatDisplayDate(DateUtils.getYesterdayDateString())
                DateFilter.THIS_WEEK -> "This Week"
                DateFilter.THIS_MONTH -> "This Month"
                DateFilter.THIS_YEAR -> "Year 2026"
                DateFilter.ALL -> "All Transactions"
            }

            try {
                val file = PdfReportGenerator.generateDayBookPdf(
                    context = context,
                    profile = prof,
                    dateLabel = dateLabel,
                    summary = currentSumm,
                    transactions = currentList,
                    language = lang
                )
                withContext(Dispatchers.Main) {
                    PdfReportGenerator.sharePdf(context, file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _snackbarMessage.value = "Error generating PDF: ${e.localizedMessage}"
                }
            }
        }
    }

    fun exportAndShareCsv(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val list = repository.getAllSnapshot()
                val file = CsvBackupHelper.exportToCsvFile(context, list)
                withContext(Dispatchers.Main) {
                    CsvBackupHelper.shareCsv(context, file)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _snackbarMessage.value = "Export failed: ${e.localizedMessage}"
                }
            }
        }
    }

    fun restoreFromCsvUri(context: Context, uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val imported = CsvBackupHelper.importFromCsv(inputStream)
                    if (imported.isNotEmpty()) {
                        repository.insertAll(imported)
                        withContext(Dispatchers.Main) {
                            _snackbarMessage.value = "Restored ${imported.size} records from CSV!"
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _snackbarMessage.value = "No valid records found in CSV file"
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _snackbarMessage.value = "Restore failed: ${e.localizedMessage}"
                }
            }
        }
    }
}
