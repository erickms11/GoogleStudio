package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PillarWeightsDao {
    @Query("SELECT * FROM pillar_weights_config WHERE id = 1")
    fun getPillarWeightsConfig(): Flow<PillarWeightsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePillarWeightsConfig(config: PillarWeightsEntity)
}
