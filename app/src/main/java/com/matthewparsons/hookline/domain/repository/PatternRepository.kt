package com.matthewparsons.hookline.domain.repository

import com.matthewparsons.hookline.domain.model.Pattern
import kotlinx.coroutines.flow.Flow

/**
 * Read/write access to the user's saved patterns. The data layer provides the
 * implementation; ViewModels and use cases depend on this interface only.
 */
interface PatternRepository {
    fun observeAll(): Flow<List<SavedPattern>>

    suspend fun getById(id: String): SavedPattern?

    /** Persists [pattern], assigns it an id and timestamp, returns the saved record. */
    suspend fun save(pattern: Pattern): SavedPattern

    suspend fun delete(id: String)
}
