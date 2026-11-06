package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.PaymentMode
import com.example.ui.theme.BankColor
import com.example.ui.theme.CashColor
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.UpiColor
import com.example.util.Localization
import java.util.Locale

data class MonthData(
    val monthName: String,
    val income: Double,
    val expense: Double
)

@Composable
fun MonthlyTrendBarChart(
    monthlyData: List<MonthData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_trend_chart"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Income vs Expense Trend",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Chart Legend
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Income", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Expense", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            val maxVal = monthlyData.maxOfOrNull { maxOf(it.income, it.expense) }?.coerceAtLeast(100.0) ?: 100.0

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                monthlyData.forEach { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.width(44.dp)
                    ) {
                        val incHeight = ((data.income / maxVal) * 90).coerceAtLeast(4.0).dp
                        val expHeight = ((data.expense / maxVal) * 90).coerceAtLeast(4.0).dp

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Income Bar
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(incHeight)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(IncomeGreen)
                            )
                            // Expense Bar
                            Box(
                                modifier = Modifier
                                    .width(14.dp)
                                    .height(expHeight)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(ExpenseRed)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = data.monthName,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryDonutPieChart(
    categoryTotals: Map<String, Double>,
    language: AppLanguage,
    title: String = "Spending & Service Distribution",
    modifier: Modifier = Modifier
) {
    val total = categoryTotals.values.sum().coerceAtLeast(1.0)
    val colorPalette = listOf(
        CscNavyDark,
        CscGold,
        IncomeGreen,
        ExpenseRed,
        CashColor,
        UpiColor,
        BankColor,
        Color(0xFFE11D48),
        Color(0xFFD97706),
        Color(0xFF0284C7)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("donut_pie_chart"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (categoryTotals.isEmpty()) {
                Text(
                    text = Localization.noTransactions(language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                // Donut Chart Canvas
                Box(
                    modifier = Modifier.size(170.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(150.dp)) {
                        var startAngle = -90f
                        val strokeWidth = 28.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        val arcSize = Size(diameter, diameter)

                        categoryTotals.entries.forEachIndexed { index, entry ->
                            val sweepAngle = ((entry.value / total) * 360f).toFloat()
                            val color = colorPalette[index % colorPalette.size]

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                            )
                            startAngle += sweepAngle
                        }
                    }

                    // Center label
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Total",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${formatAmount(total)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Legend Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryTotals.entries.take(6).forEachIndexed { index, entry ->
                        val color = colorPalette[index % colorPalette.size]
                        val name = Localization.categoryName(entry.key, entry.key, language)
                        val pct = ((entry.value / total) * 100).toInt()

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$name: $pct% (₹${formatAmount(entry.value)})",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueComparisonChart(
    income: Double,
    expense: Double,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Revenue & Expense Ratio",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            val maxVal = maxOf(income, expense, 1.0)
            val incomeRatio by animateFloatAsState(targetValue = (income / maxVal).toFloat(), label = "incomeRatio")
            val expenseRatio by animateFloatAsState(targetValue = (expense / maxVal).toFloat(), label = "expenseRatio")

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // Income Bar Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "₹${formatAmount(income)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IncomeGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height((incomeRatio * 90).coerceAtLeast(6f).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(IncomeGreen)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.transactionTypeName(com.example.data.model.TransactionType.INCOME, language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Expense Bar Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "₹${formatAmount(expense)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ExpenseRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .width(42.dp)
                            .height((expenseRatio * 90).coerceAtLeast(6f).dp)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(ExpenseRed)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = Localization.transactionTypeName(com.example.data.model.TransactionType.EXPENSE, language),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentModeBreakdownCard(
    modeTotals: Map<PaymentMode, Double>,
    modifier: Modifier = Modifier
) {
    val total = modeTotals.values.sum().coerceAtLeast(1.0)
    val cashTotal = modeTotals[PaymentMode.CASH] ?: 0.0
    val upiTotal = modeTotals[PaymentMode.ONLINE_UPI] ?: 0.0
    val bankTotal = modeTotals[PaymentMode.BANK_TRANSFER] ?: 0.0

    val cashPct = (cashTotal / total).toFloat()
    val upiPct = (upiTotal / total).toFloat()
    val bankPct = (bankTotal / total).toFloat()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Payment Mode Distribution",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Multi-segment progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    if (cashPct > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(cashPct)
                                .height(14.dp)
                                .background(CashColor)
                        )
                    }
                    if (upiPct > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(upiPct)
                                .height(14.dp)
                                .background(UpiColor)
                        )
                    }
                    if (bankPct > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(bankPct)
                                .height(14.dp)
                                .background(BankColor)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legends
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ModeLegendItem(title = "Cash", amount = cashTotal, color = CashColor)
                ModeLegendItem(title = "UPI", amount = upiTotal, color = UpiColor)
                ModeLegendItem(title = "Bank", amount = bankTotal, color = BankColor)
            }
        }
    }
}

@Composable
private fun ModeLegendItem(title: String, amount: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "₹${formatAmount(amount)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    categoryTotals: Map<String, Double>,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Service Volume Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            val sorted = categoryTotals.entries.sortedByDescending { it.value }.take(5)
            val maxCat = (sorted.firstOrNull()?.value ?: 1.0).coerceAtLeast(1.0)

            if (sorted.isEmpty()) {
                Text(
                    text = Localization.noTransactions(language),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                sorted.forEach { entry ->
                    val displayName = Localization.categoryName(entry.key, entry.key, language)
                    val progress = (entry.value / maxCat).toFloat().coerceIn(0f, 1f)

                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = displayName,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "₹${formatAmount(entry.value)}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
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
