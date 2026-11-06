package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppLanguage
import com.example.data.model.CscCategory
import com.example.data.model.PaymentMode
import com.example.data.model.TransactionEntity
import com.example.data.model.TransactionType
import com.example.ui.theme.BankColor
import com.example.ui.theme.CashColor
import com.example.ui.theme.CscGold
import com.example.ui.theme.CscNavyDark
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedLight
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenLight
import com.example.ui.theme.UpiColor
import com.example.util.Localization

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddTransactionDialog(
    initialType: TransactionType,
    editingTransaction: TransactionEntity?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (
        type: TransactionType,
        category: String,
        amount: Double,
        mode: PaymentMode,
        customerName: String,
        customerPhone: String,
        referenceNo: String,
        notes: String,
        aepsCashGiven: Double
    ) -> Unit
) {
    var selectedType by remember {
        mutableStateOf(editingTransaction?.transactionType ?: initialType)
    }

    var amountText by remember {
        mutableStateOf(
            editingTransaction?.let {
                if (it.amount % 1.0 == 0.0) it.amount.toLong().toString() else it.amount.toString()
            } ?: ""
        )
    }

    var selectedCategory by remember {
        mutableStateOf(
            editingTransaction?.category ?: if (selectedType == TransactionType.INCOME) {
                CscCategory.AADHAAR_UPDATE.id
            } else {
                CscCategory.PAPER_RIM.id
            }
        )
    }

    var selectedMode by remember {
        mutableStateOf(editingTransaction?.mode ?: PaymentMode.CASH)
    }

    var customerName by remember {
        mutableStateOf(editingTransaction?.customerName ?: "")
    }

    var customerPhone by remember {
        mutableStateOf(editingTransaction?.customerPhone ?: "")
    }

    var referenceNo by remember {
        mutableStateOf(editingTransaction?.referenceNo ?: "")
    }

    var notes by remember {
        mutableStateOf(editingTransaction?.notes ?: "")
    }

    var isAeps by remember {
        mutableStateOf(
            editingTransaction?.category == CscCategory.AEPS_WITHDRAWAL.id ||
            (editingTransaction?.aepsCashGiven ?: 0.0) > 0.0
        )
    }

    var aepsCashText by remember {
        mutableStateOf(
            editingTransaction?.aepsCashGiven?.let {
                if (it > 0) (if (it % 1.0 == 0.0) it.toLong().toString() else it.toString()) else ""
            } ?: ""
        )
    }

    var amountError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .testTag("add_transaction_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (editingTransaction != null) {
                            Localization.edit(language)
                        } else {
                            if (selectedType == TransactionType.INCOME) Localization.addIncome(language) else Localization.addExpense(language)
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = Localization.cancel(language),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Income / Expense Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Income Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedType == TransactionType.INCOME) IncomeGreen else Color.Transparent)
                            .clickable {
                                selectedType = TransactionType.INCOME
                                selectedCategory = CscCategory.AADHAAR_UPDATE.id
                            }
                            .padding(vertical = 10.dp)
                            .testTag("type_income_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Localization.transactionTypeName(TransactionType.INCOME, language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.INCOME) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Expense Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (selectedType == TransactionType.EXPENSE) ExpenseRed else Color.Transparent)
                            .clickable {
                                selectedType = TransactionType.EXPENSE
                                selectedCategory = CscCategory.PAPER_RIM.id
                                isAeps = false
                            }
                            .padding(vertical = 10.dp)
                            .testTag("type_expense_tab"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = Localization.transactionTypeName(TransactionType.EXPENSE, language),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedType == TransactionType.EXPENSE) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        amountError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    label = {
                        Text(
                            text = if (isAeps) "Commission Amount (₹)" else Localization.amountLabel(language)
                        )
                    },
                    prefix = { Text("₹ ", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Quick Amount chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(50, 100, 150, 200, 280, 500, 1000).forEach { chipAmt ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable {
                                val current = amountText.toDoubleOrNull() ?: 0.0
                                amountText = (chipAmt).toString()
                            }
                        ) {
                            Text(
                                text = "₹$chipAmt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selection
                Text(
                    text = Localization.categoryLabel(language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                val categories = if (selectedType == TransactionType.INCOME) {
                    CscCategory.incomeCategories
                } else {
                    CscCategory.expenseCategories
                }

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat.id
                        val catName = Localization.categoryName(cat.id, cat.defaultName, language)
                        val icon = getCategoryIcon(cat.id)

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) {
                                if (selectedType == TransactionType.INCOME) IncomeGreenLight else ExpenseRedLight
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                            },
                            border = if (isSelected) {
                                androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                )
                            } else null,
                            modifier = Modifier
                                .clickable {
                                    selectedCategory = cat.id
                                    if (cat.id == CscCategory.AEPS_WITHDRAWAL.id) {
                                        isAeps = true
                                    }
                                }
                                .testTag("cat_chip_${cat.id}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = catName,
                                    tint = if (isSelected) {
                                        if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                    } else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = catName,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                                    } else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Special AEPS cash out section
                if (selectedCategory == CscCategory.AEPS_WITHDRAWAL.id || isAeps) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "AEPS Cash Withdrawal Config",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = Localization.aepsCashWithdrawalNotice(language),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = aepsCashText,
                                onValueChange = { aepsCashText = it },
                                label = { Text("Cash Given to Customer (₹)") },
                                placeholder = { Text("e.g. 3000") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Payment Mode
                Text(
                    text = Localization.paymentModeLabel(language),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaymentMode.values().forEach { mode ->
                        val isSelected = selectedMode == mode
                        val label = Localization.paymentModeName(mode, language)
                        val icon = when (mode) {
                            PaymentMode.CASH -> Icons.Default.Payments
                            PaymentMode.ONLINE_UPI -> Icons.Default.QrCode
                            PaymentMode.BANK_TRANSFER -> Icons.Default.AccountBalance
                        }
                        val accent = when (mode) {
                            PaymentMode.CASH -> CashColor
                            PaymentMode.ONLINE_UPI -> UpiColor
                            PaymentMode.BANK_TRANSFER -> BankColor
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) accent.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, accent) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMode = mode }
                                .testTag("mode_${mode.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = when (mode) {
                                        PaymentMode.CASH -> "Cash"
                                        PaymentMode.ONLINE_UPI -> "UPI"
                                        PaymentMode.BANK_TRANSFER -> "Bank"
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) accent else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Customer Name & Phone
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text(Localization.customerLabel(language)) },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("customer_name_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = referenceNo,
                        onValueChange = { referenceNo = it },
                        label = { Text(Localization.refLabel(language)) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ref_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes / Remarks
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(Localization.notesLabel(language)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notes_input"),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(Localization.cancel(language))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            val parsedAmount = amountText.toDoubleOrNull()
                            if (parsedAmount == null || parsedAmount <= 0.0) {
                                amountError = true
                            } else {
                                onSave(
                                    selectedType,
                                    selectedCategory,
                                    parsedAmount,
                                    selectedMode,
                                    customerName.trim(),
                                    customerPhone.trim(),
                                    referenceNo.trim(),
                                    notes.trim(),
                                    aepsCashText.toDoubleOrNull() ?: 0.0
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedType == TransactionType.INCOME) IncomeGreen else ExpenseRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_transaction_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = Localization.save(language),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
