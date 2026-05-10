package com.matthewparsons.hookline.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PatternEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class HooklineDatabase : RoomDatabase() {
    abstract fun patternDao(): PatternDao

    companion object {
        const val NAME = "hookline.db"
    }
}
