package com.matthewparsons.hookline.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Storage representation of a saved pattern. The full domain [Pattern] is held
 * as a JSON blob in [patternJson]; the other columns are denormalized copies
 * of fields the home list needs so we don't have to deserialize JSON for every
 * row.
 */
@Entity(tableName = "patterns")
data class PatternEntity(
    @PrimaryKey val id: String,
    val createdAtEpochMs: Long,
    val patternJson: String,
    val shapeName: String,
    val yarnNumber: Int,
    val totalBaseStitches: Int,
    val estimatedYards: Double,
)
