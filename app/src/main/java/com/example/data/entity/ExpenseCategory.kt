package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_categories")
data class ExpenseCategory(
    @PrimaryKey
    val id: String, // e.g., "paper_rim", "cartridge_toner", "electricity", "shop_rent", "tea_snacks"
    val name: String,
    val defaultCost: Double = 0.0,
    val iconName: String = "inventory_2",
    val description: String = "",
    val isRecurring: Boolean = false,
    val isActive: Boolean = true
)
