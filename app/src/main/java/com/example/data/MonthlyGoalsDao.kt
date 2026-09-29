package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyGoalsDao {
    @Query("SELECT * FROM monthly_goals WHERE id = 1")
    fun getMonthlyGoals(): Flow<MonthlyGoalsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMonthlyGoals(goals: MonthlyGoalsEntity)
}
