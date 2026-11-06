package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.entity.ExpenseCategory
import com.example.data.entity.ServiceCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM service_categories WHERE isActive = 1 ORDER BY name ASC")
    fun getAllServiceCategories(): Flow<List<ServiceCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceCategories(categories: List<ServiceCategory>)

    @Query("SELECT * FROM expense_categories WHERE isActive = 1 ORDER BY name ASC")
    fun getAllExpenseCategories(): Flow<List<ExpenseCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseCategories(categories: List<ExpenseCategory>)

    @Query("SELECT COUNT(*) FROM service_categories")
    suspend fun getServiceCategoryCount(): Int

    @Query("SELECT COUNT(*) FROM expense_categories")
    suspend fun getExpenseCategoryCount(): Int
}
