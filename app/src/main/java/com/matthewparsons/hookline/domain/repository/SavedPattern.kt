package com.matthewparsons.hookline.domain.repository

import com.matthewparsons.hookline.domain.model.Pattern

/**
 * A persisted pattern: the generator output [pattern] paired with the storage
 * metadata assigned at save time. Pattern itself stays free of persistence
 * concerns (see [Pattern]).
 */
data class SavedPattern(
    val id: String,
    val createdAtEpochMs: Long,
    val pattern: Pattern,
)
