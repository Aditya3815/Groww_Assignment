package com.example.groww_assignment.di

import android.app.Application
import com.example.groww_assignment.BuildConfig.API_KEY
import com.example.groww_assignment.data.remote.api.AlphaVantageApi
import com.example.groww_assignment.data.remote.interceptors.ApiKeyInterceptor
import com.example.groww_assignment.data.remote.interceptors.CacheInterceptor
import com.example.groww_assignment.utils.Constants.BASE_URL
import com.example.groww_assignment.utils.Constants.MAX_CACHE_SIZE
import com.github.mikephil.charting.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        return Cache(
            directory = File(application.cacheDir, "http_cache"),
            maxSize = MAX_CACHE_SIZE
        )
    }

    @Provides
    @Singleton
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor(API_KEY)
    }

    @Provides
    @Singleton
    fun provideCacheInterceptor(): CacheInterceptor {
        return CacheInterceptor()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        cache: Cache,
        apiKeyInterceptor: ApiKeyInterceptor,
        cacheInterceptor: CacheInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(cacheInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAlphaVantageApi(retrofit: Retrofit): AlphaVantageApi {
        return retrofit.create(AlphaVantageApi::class.java)
    }
}