package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Atm
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.BankColor
import com.example.ui.theme.CashColor
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.UpiColor
import com.example.util.DateUtils
import com.example.util.Localization
import java.util.Locale

@Composable
fun TransactionItemCard(
    transaction: TransactionEntity,
    language: AppLanguage,
    onEdit: (TransactionEntity) -> Unit,
    onDelete: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val isIncome = transaction.transactionType == TransactionType.INCOME
    val categoryDisplay = Localization.categoryName(transaction.category, transaction.category, language)
    val icon = getCategoryIcon(transaction.category)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("txn_item_${transaction.id}")
            .clickable { onEdit(transaction) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon with Circle Shading
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isIncome) IncomeGreenLight else ExpenseRedLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = categoryDisplay,
                    tint = if (isIncome) IncomeGreen else ExpenseRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Main Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = categoryDisplay,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                // Customer Name / Note
                val subtitle = when {
                    transaction.customerName.isNotBlank() && transaction.referenceNo.isNotBlank() ->
                        "${transaction.customerName} • Ref: ${transaction.referenceNo}"
                    transaction.customerName.isNotBlank() -> transaction.customerName
                    transaction.referenceNo.isNotBlank() -> "Ref: ${transaction.referenceNo}"
                    transaction.notes.isNotBlank() -> transaction.notes
                    else -> null
                }

                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Badges: Payment mode & Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PaymentModeBadge(transaction.mode)

                    if (transaction.aepsCashGiven > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "AEPS Cash Out: ₹${transaction.aepsCashGiven.toInt()}",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = DateUtils.formatTime(transaction.timestamp),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Amount Column & Delete action
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isIncome) "+" else "-"} ₹${formatAmount(transaction.amount)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) IncomeGreen else ExpenseRed
                )

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_btn_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(Localization.delete(language)) },
            text = { Text("Are you sure you want to delete this $categoryDisplay entry of ₹${formatAmount(transaction.amount)}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(transaction)
                    }
                ) {
                    Text(Localization.delete(language), color = ExpenseRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(Localization.cancel(language))
                }
            }
        )
    }
}

@Composable
fun PaymentModeBadge(mode: PaymentMode) {
    val (label, bg, fg) = when (mode) {
        PaymentMode.CASH -> Triple("Cash", CashColor.copy(alpha = 0.12f), CashColor)
        PaymentMode.ONLINE_UPI -> Triple("UPI", UpiColor.copy(alpha = 0.12f), UpiColor)
        PaymentMode.BANK_TRANSFER -> Triple("Bank", BankColor.copy(alpha = 0.12f), BankColor)
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (mode) {
                PaymentMode.CASH -> Icons.Default.Payments
                PaymentMode.ONLINE_UPI -> Icons.Default.QrCode
                PaymentMode.BANK_TRANSFER -> Icons.Default.AccountBalance
            }
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = fg,
                modifier = Modifier.size(10.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = fg
            )
        }
    }
}

fun getCategoryIcon(categoryId: String): ImageVector {
    return when (categoryId.lowercase()) {
        "aadhaar" -> Icons.Default.Badge
        "pan" -> Icons.Default.CreditCard
        "ayushman" -> Icons.Default.HealthAndSafety
        "pm_kisan" -> Icons.Default.Agriculture
        "aeps" -> Icons.Default.Atm
        "bill_pay" -> Icons.Default.ReceiptLong
        "online_form" -> Icons.Default.Description
        "xerox" -> Icons.Default.Print
        "paper_rim" -> Icons.Default.Inventory2
        "cartridge_toner" -> Icons.Default.Colorize
        "electricity" -> Icons.Default.Bolt
        "shop_rent" -> Icons.Default.Store
        "tea_snacks" -> Icons.Default.LocalCafe
        "internet" -> Icons.Default.Wifi
        "maintenance" -> Icons.Default.Build
        else -> Icons.Default.MoneyOff
    }
}

private fun formatAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        amount.toLong().toString()
    } else {
        String.format(Locale.ENGLISH, "%.2f", amount)
    }
}
