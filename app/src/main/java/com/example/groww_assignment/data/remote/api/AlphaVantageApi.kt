package com.example.groww_assignment.data.remote.api

import com.example.groww_assignment.data.remote.dto.CompanyOverviewDto
import com.example.groww_assignment.data.remote.dto.SearchResultDto
import com.example.groww_assignment.data.remote.dto.TimeSeriesDto
import com.example.groww_assignment.data.remote.dto.TopGainersLosersDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {
    @GET("query")
    suspend fun getTopGainersLosers(
        @Query("function") function: String = "TOP_GAINERS_LOSERS"
    ): Response<TopGainersLosersDto>

    @GET("query")
    suspend fun getCompanyOverview(
        @Query("function") function: String = "OVERVIEW",
        @Query("symbol") symbol: String
    ): Response<CompanyOverviewDto>

    @GET("query")
    suspend fun getTimeSeriesDaily(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("outputsize") outputsize: String = "compact"
    ): Response<TimeSeriesDto>

    @GET("query")
    suspend fun searchSymbol(
        @Query("function") function: String = "SYMBOL_SEARCH",
        @Query("keywords") keywords: String
    ): Response<SearchResultDto>
}