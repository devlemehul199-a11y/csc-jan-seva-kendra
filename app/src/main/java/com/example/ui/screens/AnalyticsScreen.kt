package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DailySummary
import com.example.ui.components.CategoryBreakdownCard
import com.example.ui.components.CategoryDonutPieChart
import com.example.ui.components.MonthData
import com.example.ui.components.MonthlyTrendBarChart
import com.example.ui.components.PaymentModeBreakdownCard
import com.example.ui.components.RevenueComparisonChart
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.DateFilter
import com.example.util.Localization
import java.util.Locale

@Composable
fun AnalyticsScreen(
    summary: DailySummary,
    monthlyTrends: List<MonthData>,
    language: AppLanguage,
    selectedFilter: DateFilter,
    onFilterSelect: (DateFilter) -> Unit,
    onExportPdf: () -> Unit,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header with Export Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Advanced Financial Reports",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Monthly & Yearly Day-Book Breakdown",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = onExportPdf,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("analytics_export_pdf_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = onExportCsv,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("analytics_export_csv_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "CSV",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CSV", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Date Filter Chips (Today, Yesterday, This Week, This Month, This Year, All)
        item {
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
                        modifier = Modifier.clickable { onFilterSelect(filter) }
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

        // Quick Stats KPI Row
        item {
            val netMargin = if (summary.totalIncome > 0) {
                ((summary.netBalance / summary.totalIncome) * 100).toInt()
            } else 0

            val avgTxn = if (summary.incomeCount > 0) {
                summary.totalIncome / summary.incomeCount
            } else 0.0

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Net Profit Margin",
                    value = "$netMargin%",
                    subtitle = "${summary.transactionCount} transactions",
                    color = if (netMargin >= 0) IncomeGreen else ExpenseRed,
                    modifier = Modifier.weight(1f)
                )

                StatMetricCard(
                    title = "Avg Service Ticket",
                    value = "₹${formatAmount(avgTxn)}",
                    subtitle = "${summary.incomeCount} customer receipts",
                    color = CscNavyDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Monthly Trend Multi-Bar Chart
        item {
            MonthlyTrendBarChart(
                monthlyData = monthlyTrends
            )
        }

        // Category Donut Pie Chart (Visualized Distribution)
        item {
            CategoryDonutPieChart(
                categoryTotals = summary.categoryTotals,
                language = language,
                title = "Category-Wise Spending & Service Distribution"
            )
        }

        // Revenue & Expense Ratio
        item {
            RevenueComparisonChart(
                income = summary.totalIncome,
                expense = summary.totalExpense,
                language = language
            )
        }

        // Payment Mode Distribution (Cash vs UPI vs Bank)
        item {
            PaymentModeBreakdownCard(
                modeTotals = summary.paymentModeTotals
            )
        }

        // Category Ranked Progress Breakdown
        item {
            CategoryBreakdownCard(
                categoryTotals = summary.categoryTotals,
                language = language
            )
        }
    }
}

@Composable
private fun StatMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

private fun formatAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        amount.toLong().toString()
    } else {
        String.format(Locale.ENGLISH, "%.2f", amount)
    }
}
