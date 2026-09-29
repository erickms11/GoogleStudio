package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pillar_weights_config")
data class PillarWeightsEntity(
    @PrimaryKey
    val id: Int = 1,
    val workUnderloadImpactName: String = "MODERATE",
    val workOverloadImpactName: String = "MODERATE",
    val healthNeglectImpactName: String = "MODERATE",
    val digitalNoiseImpactName: String = "MODERATE",
    val leisureOverloadImpactName: String = "MODERATE"
)
