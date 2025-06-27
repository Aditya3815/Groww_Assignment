package com.example.groww_assignment.di

import android.content.Context
import androidx.room.Room
import com.example.groww_assignment.data.local.database.StocksDatabase
import com.example.groww_assignment.data.local.database.dao.StockDao
import com.example.groww_assignment.data.local.database.dao.WatchlistDao
import com.example.groww_assignment.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideStocksDatabase(@ApplicationContext context: Context): StocksDatabase {
        return Room.databaseBuilder(
            context,
            StocksDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideStockDao(database: StocksDatabase): StockDao {
        return database.stockDao()
    }

    @Provides
    fun provideWatchlistDao(database: StocksDatabase): WatchlistDao {
        return database.watchlistDao()
    }
}