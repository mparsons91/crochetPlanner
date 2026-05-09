package com.matthewparsons.hookline.di

import android.content.Context
import androidx.room.Room
import com.matthewparsons.hookline.data.local.HooklineDatabase
import com.matthewparsons.hookline.data.local.PatternDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HooklineDatabase =
        Room.databaseBuilder(context, HooklineDatabase::class.java, HooklineDatabase.NAME).build()

    @Provides
    fun providePatternDao(database: HooklineDatabase): PatternDao = database.patternDao()

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }
}
