package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {

    @Query("SELECT * FROM daily_checkins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<CheckInEntity>>

    @Query("SELECT * FROM daily_checkins ORDER BY timestamp DESC LIMIT 1")
    fun getLatestCheckIn(): Flow<CheckInEntity?>

    @Query("SELECT * FROM daily_checkins ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCheckIns(limit: Int): Flow<List<CheckInEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInEntity): Long

    @Query("UPDATE daily_checkins SET questCompleted = :completed WHERE id = :id")
    suspend fun updateQuestStatus(id: Long, completed: Boolean)

    @Query("DELETE FROM daily_checkins WHERE id = :id")
    suspend fun deleteCheckInById(id: Long)

    @Query("SELECT COUNT(*) FROM daily_checkins")
    suspend fun countCheckIns(): Int

    @Query("DELETE FROM daily_checkins")
    suspend fun deleteAllCheckIns()
}
