package com.example.groww_assignment.di

import com.example.groww_assignment.data.local.cache.CacheManager
import com.example.groww_assignment.data.local.database.dao.StockDao
import com.example.groww_assignment.data.local.database.dao.WatchlistDao
import com.example.groww_assignment.data.remote.api.AlphaVantageApi
import com.example.groww_assignment.data.remote.api.NetworkService
import com.example.groww_assignment.data.repository.StocksRepositoryImpl
import com.example.groww_assignment.data.repository.WatchlistRepositoryImpl
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.repository.WatchlistRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNetworkService(): NetworkService {
        return NetworkService()
    }

    @Provides
    @Singleton
    fun provideCacheManager(): CacheManager {
        return CacheManager()
    }

    @Provides
    @Singleton
    fun provideStocksRepository(
        api: AlphaVantageApi,
        stockDao: StockDao,
        networkService: NetworkService
    ): StocksRepository {
        return StocksRepositoryImpl(api, stockDao, networkService)
    }

    @Provides
    @Singleton
    fun provideWatchlistRepository(
        watchlistDao: WatchlistDao,
        stockDao: StockDao
    ): WatchlistRepository {
        return WatchlistRepositoryImpl(watchlistDao, stockDao)
    }
}