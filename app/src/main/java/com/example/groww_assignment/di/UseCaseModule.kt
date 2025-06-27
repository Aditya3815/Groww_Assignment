package com.example.groww_assignment.di

import com.example.groww_assignment.data.local.cache.CacheManager
import com.example.groww_assignment.data.local.datastore.PreferencesManager
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.repository.StocksRepository
import com.example.groww_assignment.domain.repository.WatchlistRepository
import com.example.groww_assignment.domain.usecase.cache.CacheManagementUseCase
import com.example.groww_assignment.domain.usecase.composite.GetStockWithWatchlistStatusUseCase
import com.example.groww_assignment.domain.usecase.composite.SearchAndValidateStocksUseCase
import com.example.groww_assignment.domain.usecase.composite.WatchlistOperationsUseCase
import com.example.groww_assignment.domain.usecase.network.NetworkStatusUseCase
import com.example.groww_assignment.domain.usecase.pagination.PaginationUseCase
import com.example.groww_assignment.domain.usecase.preferences.PreferencesUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetPopularStocksUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStockDetailUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStockTimeSeriesUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStocksByTypeUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetStocksFromCacheUseCase
import com.example.groww_assignment.domain.usecase.stocks.GetTopGainersLosersUseCase
import com.example.groww_assignment.domain.usecase.stocks.SearchStocksUseCase
import com.example.groww_assignment.domain.usecase.validation.ValidateStockSymbolUseCase
import com.example.groww_assignment.domain.usecase.validation.ValidateWatchlistNameUseCase
import com.example.groww_assignment.domain.usecase.watchlist.AddToWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.CheckWatchlistLimitsUseCase
import com.example.groww_assignment.domain.usecase.watchlist.CreateWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.DeleteWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetDefaultWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetStocksInWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetWatchlistByIdUseCase
import com.example.groww_assignment.domain.usecase.watchlist.GetWatchlistsUseCase
import com.example.groww_assignment.domain.usecase.watchlist.IsStockInWatchlistUseCase
import com.example.groww_assignment.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.example.groww_assignment.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    // Stock Use Cases
    @Provides
    @Singleton
    fun provideGetTopGainersLosersUseCase(
        repository: StocksRepository
    ): GetTopGainersLosersUseCase {
        return GetTopGainersLosersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStockDetailUseCase(
        repository: StocksRepository
    ): GetStockDetailUseCase {
        return GetStockDetailUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPopularStocksUseCase(
        repository: StocksRepository
    ): GetPopularStocksUseCase {
        return GetPopularStocksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchStocksUseCase(
        repository: StocksRepository
    ): SearchStocksUseCase {
        return SearchStocksUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStockTimeSeriesUseCase(
        repository: StocksRepository
    ): GetStockTimeSeriesUseCase {
        return GetStockTimeSeriesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStocksFromCacheUseCase(
        repository: StocksRepository
    ): GetStocksFromCacheUseCase {
        return GetStocksFromCacheUseCase(repository)
    }

    // Watchlist Use Cases
    @Provides
    @Singleton
    fun provideGetWatchlistsUseCase(
        repository: WatchlistRepository
    ): GetWatchlistsUseCase {
        return GetWatchlistsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideCreateWatchlistUseCase(
        repository: WatchlistRepository
    ): CreateWatchlistUseCase {
        return CreateWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddToWatchlistUseCase(
        repository: WatchlistRepository
    ): AddToWatchlistUseCase {
        return AddToWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideRemoveFromWatchlistUseCase(
        repository: WatchlistRepository
    ): RemoveFromWatchlistUseCase {
        return RemoveFromWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteWatchlistUseCase(
        repository: WatchlistRepository
    ): DeleteWatchlistUseCase {
        return DeleteWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetStocksInWatchlistUseCase(
        repository: WatchlistRepository
    ): GetStocksInWatchlistUseCase {
        return GetStocksInWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideIsStockInWatchlistUseCase(
        repository: WatchlistRepository
    ): IsStockInWatchlistUseCase {
        return IsStockInWatchlistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetDefaultWatchlistUseCase(
        watchlistRepository: WatchlistRepository,
        preferencesUseCase: PreferencesUseCase,
        createWatchlistUseCase: CreateWatchlistUseCase
    ): GetDefaultWatchlistUseCase {
        return GetDefaultWatchlistUseCase(
            watchlistRepository,
            preferencesUseCase,
            createWatchlistUseCase
        )
    }

    @Provides
    @Singleton
    fun provideCheckWatchlistLimitsUseCase(
        repository: WatchlistRepository,
        getStocksInWatchlistUseCase: GetStocksInWatchlistUseCase
    ): CheckWatchlistLimitsUseCase {
        return CheckWatchlistLimitsUseCase(repository, getStocksInWatchlistUseCase)
    }

    @Provides
    @Singleton
    fun provideGetWatchlistByIdUseCase(
        repository: WatchlistRepository
    ): GetWatchlistByIdUseCase {
        return GetWatchlistByIdUseCase(repository)
    }

    // Validation Use Cases
    @Provides
    @Singleton
    fun provideValidateStockSymbolUseCase(): ValidateStockSymbolUseCase {
        return ValidateStockSymbolUseCase()
    }

    @Provides
    @Singleton
    fun provideValidateWatchlistNameUseCase(): ValidateWatchlistNameUseCase {
        return ValidateWatchlistNameUseCase()
    }

    // Utility Use Cases
    @Provides
    @Singleton
    fun providePaginationUseCase(): PaginationUseCase<Stock> {
        return PaginationUseCase()
    }

    @Provides
    @Singleton
    fun provideCacheManagementUseCase(
        cacheManager: CacheManager
    ): CacheManagementUseCase {
        return CacheManagementUseCase(cacheManager)
    }

    @Provides
    @Singleton
    fun providePreferencesUseCase(
        preferencesManager: PreferencesManager
    ): PreferencesUseCase {
        return PreferencesUseCase(preferencesManager)
    }

    @Provides
    @Singleton
    fun provideNetworkStatusUseCase(
        networkUtils: NetworkUtils
    ): NetworkStatusUseCase {
        return NetworkStatusUseCase(networkUtils)
    }

    @Provides
    @Singleton
    fun provideWatchlistOperationsUseCase(
        createWatchlistUseCase: CreateWatchlistUseCase,
        addToWatchlistUseCase: AddToWatchlistUseCase,
        validateWatchlistNameUseCase: ValidateWatchlistNameUseCase
    ): WatchlistOperationsUseCase {
        return WatchlistOperationsUseCase(
            createWatchlistUseCase,
            addToWatchlistUseCase,
            validateWatchlistNameUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetStockWithWatchlistStatusUseCase(
        getStockDetailUseCase: GetStockDetailUseCase,
        isStockInWatchlistUseCase: IsStockInWatchlistUseCase
    ): GetStockWithWatchlistStatusUseCase {
        return GetStockWithWatchlistStatusUseCase(
            getStockDetailUseCase,
            isStockInWatchlistUseCase
        )
    }

    @Provides
    @Singleton
    fun provideGetStocksByTypeUseCase(
        repository: StocksRepository
    ): GetStocksByTypeUseCase {
        return GetStocksByTypeUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchAndValidateStocksUseCase(
        searchStocksUseCase: SearchStocksUseCase,
        validateStockSymbolUseCase: ValidateStockSymbolUseCase
    ) : SearchAndValidateStocksUseCase {
        return SearchAndValidateStocksUseCase(
            searchStocksUseCase,
            validateStockSymbolUseCase
        )
    }



}