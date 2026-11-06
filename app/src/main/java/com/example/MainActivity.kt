package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.annotation.SuppressLint
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.AppLanguage
import com.example.data.model.TransactionType
import com.example.ui.components.AddTransactionDialog
import com.example.ui.components.VoiceInputDialog
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.screens.performSignOut
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.KhataViewModel
import com.example.util.Localization

enum class Screen(val title: String, val icon: ImageVector) {
    DASHBOARD("Day-Book", Icons.Default.Dashboard),
    TRANSACTIONS("Records", Icons.Default.ReceiptLong),
    ANALYTICS("Analytics", Icons.Default.BarChart),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: KhataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: KhataViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    var isWebPortalMode by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }

    // If Web Portal Mode is active, render hardware-accelerated WebView loading the single-file portal
    if (isWebPortalMode) {
        WebPortalView(
            onSwitchToNative = { isWebPortalMode = false }
        )
        return
    }

    // State observation
    val summary by viewModel.summary.collectAsStateWithLifecycle()
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val monthlyTrends by viewModel.monthlyTrends.collectAsStateWithLifecycle()
    val language by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val profile by viewModel.vleProfile.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedTypeFilter by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()

    val showAddDialog by viewModel.showAddDialog.collectAsStateWithLifecycle()
    val editingTransaction by viewModel.editingTransaction.collectAsStateWithLifecycle()
    val initialDialogType by viewModel.initialDialogType.collectAsStateWithLifecycle()
    val showVoiceDialog by viewModel.showVoiceDialog.collectAsStateWithLifecycle()
    val showAuthDialog by viewModel.showAuthDialog.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    // Handle back button when not on dashboard
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        currentScreen = Screen.DASHBOARD
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Screen.values().forEach { screen ->
                    val isSelected = currentScreen == screen
                    val label = when (screen) {
                        Screen.DASHBOARD -> Localization.appSubtitle(language).take(12)
                        Screen.TRANSACTIONS -> Localization.filterAll(language).take(10)
                        Screen.ANALYTICS -> "Analytics"
                        Screen.SETTINGS -> "Settings"
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentScreen = screen },
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CscNavyDark,
                            selectedTextColor = CscNavyDark,
                            indicatorColor = CscGold.copy(alpha = 0.25f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_item_${screen.name}")
                    )
                }
            }
        },
        floatingActionButton = {
            // Quick FAB on Dashboard and Transactions screens
            if (currentScreen == Screen.DASHBOARD || currentScreen == Screen.TRANSACTIONS) {
                FloatingActionButton(
                    onClick = { viewModel.openAddDialog(TransactionType.INCOME) },
                    containerColor = IncomeGreen,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_add_transaction")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = Localization.addIncome(language),
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    summary = summary,
                    transactions = transactions,
                    language = language,
                    profile = profile,
                    selectedFilter = selectedFilter,
                    onFilterSelect = { viewModel.setFilter(it) },
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onAddIncome = { viewModel.openAddDialog(TransactionType.INCOME) },
                    onAddExpense = { viewModel.openAddDialog(TransactionType.EXPENSE) },
                    onVoiceEntry = { viewModel.openVoiceDialog() },
                    onEditTransaction = { viewModel.openEditDialog(it) },
                    onDeleteTransaction = { viewModel.deleteTransaction(it) },
                    onExportPdf = { viewModel.generateAndSharePdf(context) },
                    onNavigateToTransactions = { currentScreen = Screen.TRANSACTIONS }
                )

                Screen.TRANSACTIONS -> TransactionsScreen(
                    transactions = transactions,
                    language = language,
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter,
                    selectedTypeFilter = selectedTypeFilter,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterSelect = { viewModel.setFilter(it) },
                    onTypeFilterSelect = { viewModel.setTypeFilter(it) },
                    onEditTransaction = { viewModel.openEditDialog(it) },
                    onDeleteTransaction = { viewModel.deleteTransaction(it) },
                    onExportPdf = { viewModel.generateAndSharePdf(context) },
                    onExportCsv = { viewModel.exportAndShareCsv(context) }
                )

                Screen.ANALYTICS -> AnalyticsScreen(
                    summary = summary,
                    monthlyTrends = monthlyTrends,
                    language = language,
                    selectedFilter = selectedFilter,
                    onFilterSelect = { viewModel.setFilter(it) },
                    onExportPdf = { viewModel.generateAndSharePdf(context) },
                    onExportCsv = { viewModel.exportAndShareCsv(context) }
                )

                Screen.SETTINGS -> SettingsScreen(
                    profile = profile,
                    language = language,
                    currentUser = currentUser,
                    syncStatus = syncStatus,
                    syncMessage = syncMessage,
                    onLanguageChange = { viewModel.setLanguage(it) },
                    onProfileUpdate = { viewModel.updateProfile(it) },
                    onSyncToCloud = { viewModel.syncToCloud() },
                    onRestoreFromCloud = { viewModel.restoreFromCloud() },
                    onOpenAuthDialog = { viewModel.openAuthDialog() },
                    onSignOut = {
                        performSignOut(context, credentialManager, scope) {
                            viewModel.clearSnackbar()
                        }
                    },
                    onExportPdf = { viewModel.generateAndSharePdf(context) },
                    onExportCsv = { viewModel.exportAndShareCsv(context) },
                    onRestoreCsv = { uri -> viewModel.restoreFromCsvUri(context, uri) }
                )
            }

            // Quick Floating Chip to Return to Web Portal
            Surface(
                shape = CircleShape,
                color = CscNavyDark.copy(alpha = 0.90f),
                shadowElevation = 6.dp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 12.dp)
            ) {
                AssistChip(
                    onClick = { isWebPortalMode = true },
                    label = { Text("🌐 વેબ પોર્ટલ", fontSize = 11.sp, color = CscGold) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CscGold.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }

    // Add / Edit Transaction Dialog
    if (showAddDialog) {
        AddTransactionDialog(
            initialType = initialDialogType,
            editingTransaction = editingTransaction,
            language = language,
            onDismiss = { viewModel.closeAddDialog() },
            onSave = { type, category, amount, mode, custName, custPhone, refNo, notes, aepsCash ->
                viewModel.saveTransaction(
                    type = type,
                    category = category,
                    amount = amount,
                    mode = mode,
                    customerName = custName,
                    customerPhone = custPhone,
                    referenceNo = refNo,
                    notes = notes,
                    aepsCashGiven = aepsCash
                )
            }
        )
    }

    // Voice Input Dialog
    if (showVoiceDialog) {
        VoiceInputDialog(
            currentLanguage = language,
            onDismiss = { viewModel.closeVoiceDialog() },
            onApply = { result ->
                viewModel.applyVoiceResultAndOpenAdd(result)
            }
        )
    }

    // Cloud Authentication Dialog / Screen
    if (showAuthDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.closeAuthDialog() },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AuthScreen(
                onAuthSuccess = { viewModel.closeAuthDialog() },
                onContinueOffline = { viewModel.closeAuthDialog() }
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPortalView(
    modifier: Modifier = Modifier,
    onSwitchToNative: () -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    BackHandler(enabled = webViewRef?.canGoBack() == true) {
        webViewRef?.goBack()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = object : WebChromeClient() {
                        override fun onPermissionRequest(request: PermissionRequest) {
                            request.grant(request.resources)
                        }
                    }
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        databaseEnabled = true
                        allowFileAccess = true
                        allowContentAccess = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(false)
                    }
                    loadUrl("file:///android_asset/index.html")
                    webViewRef = this
                }
            }
        )

        // Floating Mode Switch Button (Top Right Pill)
        Surface(
            shape = CircleShape,
            color = CscNavyDark.copy(alpha = 0.90f),
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 12.dp)
        ) {
            AssistChip(
                onClick = onSwitchToNative,
                label = { Text("📱 નેટિવ મોડ", fontSize = 11.sp, color = CscGold) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CscGold.copy(alpha = 0.4f)),
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}
