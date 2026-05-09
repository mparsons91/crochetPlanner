package com.matthewparsons.hookline.data.repository

import com.matthewparsons.hookline.data.local.PatternDao
import com.matthewparsons.hookline.data.local.toEntity
import com.matthewparsons.hookline.data.local.toSavedPattern
import com.matthewparsons.hookline.domain.model.Pattern
import com.matthewparsons.hookline.domain.repository.PatternRepository
import com.matthewparsons.hookline.domain.repository.SavedPattern
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class PatternRepositoryImpl @Inject constructor(
    private val dao: PatternDao,
    private val json: Json,
) : PatternRepository {

    override fun observeAll(): Flow<List<SavedPattern>> =
        dao.observeAll().map { list -> list.map { it.toSavedPattern(json) } }

    override suspend fun getById(id: String): SavedPattern? =
        dao.getById(id)?.toSavedPattern(json)

    override suspend fun save(pattern: Pattern): SavedPattern {
        val saved = SavedPattern(
            id = UUID.randomUUID().toString(),
            createdAtEpochMs = System.currentTimeMillis(),
            pattern = pattern,
        )
        dao.upsert(saved.toEntity(json))
        return saved
    }

    override suspend fun delete(id: String) {
        dao.delete(id)
    }
}
