package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_categories")
data class ServiceCategory(
    @PrimaryKey
    val id: String, // e.g., "aadhaar", "pan", "ayushman", "pm_kisan", "aeps", "xerox"
    val name: String,
    val defaultFee: Double = 0.0,
    val commissionRate: Double = 0.0,
    val iconName: String = "category",
    val description: String = "",
    val isGovService: Boolean = true,
    val isActive: Boolean = true
)
