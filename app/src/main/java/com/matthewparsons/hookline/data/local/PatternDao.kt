package com.matthewparsons.hookline.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternDao {

    @Query("SELECT * FROM patterns ORDER BY createdAtEpochMs DESC")
    fun observeAll(): Flow<List<PatternEntity>>

    @Query("SELECT * FROM patterns WHERE id = :id")
    suspend fun getById(id: String): PatternEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: PatternEntity)

    @Query("DELETE FROM patterns WHERE id = :id")
    suspend fun delete(id: String)
}
