package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DailySummary
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.data.model.VleProfile
import com.example.ui.components.SummaryCard
import com.example.ui.components.TransactionItemCard
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.DateFilter
import com.example.util.Localization

@Composable
fun DashboardScreen(
    summary: DailySummary,
    transactions: List<TransactionEntity>,
    language: AppLanguage,
    profile: VleProfile,
    selectedFilter: DateFilter,
    onFilterSelect: (DateFilter) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit,
    onVoiceEntry: () -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    onExportPdf: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Center Header Bar
        item {
            CenterTopBar(
                profile = profile,
                language = language,
                onLanguageChange = onLanguageChange,
                onExportPdf = onExportPdf
            )
        }

        // Date Filter Chips
        item {
            DateFilterChipsRow(
                selectedFilter = selectedFilter,
                language = language,
                onFilterSelect = onFilterSelect
            )
        }

        // Executive Day-Book Summary Card
        item {
            SummaryCard(
                summary = summary,
                language = language
            )
        }

        // Voice Entry Banner
        item {
            VoiceAssistantBanner(
                language = language,
                onVoiceClick = onVoiceEntry
            )
        }

        // Quick Income & Expense Logging Buttons
        item {
            QuickActionsRow(
                language = language,
                onAddIncome = onAddIncome,
                onAddExpense = onAddExpense
            )
        }

        // Recent Transactions Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Day-Book Records",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                TextButton(
                    onClick = onNavigateToTransactions,
                    modifier = Modifier.testTag("see_all_button")
                ) {
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "See All",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // List of recent transactions (take first 6)
        if (transactions.isEmpty()) {
            item {
                EmptyTransactionsCard(language = language, onAddClick = onAddIncome)
            }
        } else {
            items(
                items = transactions.take(6),
                key = { it.id }
            ) { item ->
                TransactionItemCard(
                    transaction = item,
                    language = language,
                    onEdit = onEditTransaction,
                    onDelete = onDeleteTransaction
                )
            }
        }
    }
}

@Composable
private fun CenterTopBar(
    profile: VleProfile,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onExportPdf: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CscNavyDark
                ) {
                    Text(
                        text = "CSC",
                        color = CscGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = profile.centerName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
            Text(
                text = "${profile.vleName} • CSC ID: ${profile.cscId}",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // PDF Quick Export
            IconButton(
                onClick = onExportPdf,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("export_pdf_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = Localization.exportPdf(language),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Language Toggle Switcher
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.testTag("language_toggle")
            ) {
                Row(modifier = Modifier.padding(2.dp)) {
                    AppLanguage.values().forEach { lang ->
                        val isSelected = language == lang
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) CscNavyDark else Color.Transparent)
                                .clickable { onLanguageChange(lang) }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("lang_btn_${lang.code}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lang.shortLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DateFilterChipsRow(
    selectedFilter: DateFilter,
    language: AppLanguage,
    onFilterSelect: (DateFilter) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DateFilter.values().forEach { filter ->
            val isSelected = selectedFilter == filter
            val label = when (filter) {
                DateFilter.TODAY -> Localization.filterToday(language)
                DateFilter.YESTERDAY -> Localization.filterYesterday(language)
                DateFilter.THIS_WEEK -> Localization.filterThisWeek(language)
                DateFilter.THIS_MONTH -> Localization.filterThisMonth(language)
                DateFilter.THIS_YEAR -> "Year 2026"
                DateFilter.ALL -> Localization.filterAll(language)
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) CscNavyDark else MaterialTheme.colorScheme.surface,
                border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)) else null,
                modifier = Modifier
                    .clickable { onFilterSelect(filter) }
                    .testTag("filter_chip_${filter.name}")
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun VoiceAssistantBanner(
    language: AppLanguage,
    onVoiceClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVoiceClick() }
            .testTag("voice_assistant_banner"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(CscGold),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = Localization.voiceEntry(language),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Speak in ગુજરાતી / हिन्दी / English to record txn",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            FilledTonalButton(
                onClick = onVoiceClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Speak", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    language: AppLanguage,
    onAddIncome: () -> Unit,
    onAddExpense: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onAddIncome,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("quick_add_income_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = Localization.addIncome(language),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = Localization.addIncome(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onAddExpense,
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("quick_add_expense_btn"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = Localization.addExpense(language),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = Localization.addExpense(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyTransactionsCard(
    language: AppLanguage,
    onAddClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = Localization.noTransactions(language),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(Localization.addIncome(language))
            }
        }
    }
}
