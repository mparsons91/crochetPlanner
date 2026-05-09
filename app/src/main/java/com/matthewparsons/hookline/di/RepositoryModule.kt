package com.matthewparsons.hookline.di

import com.matthewparsons.hookline.data.repository.PatternRepositoryImpl
import com.matthewparsons.hookline.domain.repository.PatternRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPatternRepository(impl: PatternRepositoryImpl): PatternRepository
}
