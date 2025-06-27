package com.example.groww_assignment.data.mappers

import android.annotation.SuppressLint
import com.example.groww_assignment.data.local.database.entities.StockEntity
import com.example.groww_assignment.data.remote.dto.CompanyOverviewDto
import com.example.groww_assignment.data.remote.dto.SearchMatchDto
import com.example.groww_assignment.data.remote.dto.StockItemDto
import com.example.groww_assignment.data.remote.dto.TimeSeriesDto
import com.example.groww_assignment.data.remote.dto.TopGainersLosersDto
import com.example.groww_assignment.domain.model.DailyData
import com.example.groww_assignment.domain.model.SearchResult
import com.example.groww_assignment.domain.model.Stock
import com.example.groww_assignment.domain.model.StockDetail
import com.example.groww_assignment.domain.model.TimeSeriesData
import com.example.groww_assignment.domain.model.TopGainersLosers

object StockMapper {
    fun fromStockItemDto(dto: StockItemDto): Stock {
        return Stock(
            symbol = dto.ticker.orEmpty(),
            name = "",
            price = dto.price?.toDoubleOrNull() ?: 0.0,
            change = dto.changeAmount?.toDoubleOrNull() ?: 0.0,
            changePercent = dto.changePercentage.orEmpty(),
            volume = dto.volume?.toLongOrNull() ?: 0L
        )
    }

    fun fromTopGainersLosersDto(dto: TopGainersLosersDto): TopGainersLosers {
        return TopGainersLosers(
            lastUpdated = dto.lastUpdated.orEmpty(),
            topGainers = dto.topGainers?.map { fromStockItemDto(it) } ?: emptyList(),
            topLosers = dto.topLosers?.map { fromStockItemDto(it) } ?: emptyList(),
            mostActivelyTraded = dto.mostActivelyTraded?.map { fromStockItemDto(it) } ?: emptyList()
        )
    }

    fun fromCompanyOverviewDto(dto: CompanyOverviewDto): StockDetail {
        return StockDetail(
            symbol = dto.symbol.orEmpty(),
            name = dto.name.orEmpty(),
            description = dto.description.orEmpty(),
            exchange = dto.exchange.orEmpty(),
            currency = dto.currency.orEmpty(),
            country = dto.country.orEmpty(),
            sector = dto.sector.orEmpty(),
            industry = dto.industry.orEmpty(),
            marketCap = dto.marketCapitalization.orEmpty(),
            peRatio = dto.peRatio.orEmpty(),
            eps = dto.eps.orEmpty(),
            dividendYield = dto.dividendYield.orEmpty(),
            fiftyTwoWeekHigh = dto.fiftyTwoWeekHigh.orEmpty(),
            fiftyTwoWeekLow = dto.fiftyTwoWeekLow.orEmpty(),
            currentPrice = 0.0, // Will be updated from time series
            change = 0.0,
            changePercent = "",
            volume = 0L,
            beta = dto.beta.orEmpty(),
            address = dto.address.orEmpty()
        )
    }

    fun fromTimeSeriesDto(dto: TimeSeriesDto): TimeSeriesData? {
        val metaData = dto.metaData ?: return null
        val timeSeries = dto.timeSeries ?: return null

        val dailyDataList = timeSeries.map { (date, dailyDto) ->
            DailyData(
                date = date,
                open = dailyDto.open?.toDoubleOrNull() ?: 0.0,
                high = dailyDto.high?.toDoubleOrNull() ?: 0.0,
                low = dailyDto.low?.toDoubleOrNull() ?: 0.0,
                close = dailyDto.close?.toDoubleOrNull() ?: 0.0,
                volume = dailyDto.volume?.toLongOrNull() ?: 0L
            )
        }.sortedByDescending { it.date }

        return TimeSeriesData(
            symbol = metaData.symbol.orEmpty(),
            lastRefreshed = metaData.lastRefreshed.orEmpty(),
            timeZone = metaData.timeZone.orEmpty(),
            dailyData = dailyDataList
        )
    }

    fun fromSearchMatchDto(dto: SearchMatchDto): SearchResult {
        return SearchResult(
            symbol = dto.symbol.orEmpty(),
            name = dto.name.orEmpty(),
            type = dto.type.orEmpty(),
            region = dto.region.orEmpty(),
            marketOpen = dto.marketOpen.orEmpty(),
            marketClose = dto.marketClose.orEmpty(),
            timezone = dto.timezone.orEmpty(),
            currency = dto.currency.orEmpty(),
            matchScore = dto.matchScore?.toDoubleOrNull() ?: 0.0
        )
    }

    fun fromEntity(entity: StockEntity): Stock {
        return Stock(
            symbol = entity.symbol,
            name = entity.name,
            price = entity.price,
            change = entity.change,
            changePercent = entity.changePercent,
            volume = entity.volume
        )
    }

    fun toEntity(stock: Stock): StockEntity {
        return StockEntity(
            symbol = stock.symbol,
            name = stock.name,
            price = stock.price,
            change = stock.change,
            changePercent = stock.changePercent,
            volume = stock.volume,
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun updateStockWithDetail(stock: Stock, detail: StockDetail): Stock {
        return stock.copy(
            name = detail.name.ifEmpty { stock.name }
        )
    }

    @SuppressLint("DefaultLocale")
    fun updateStockDetailWithPrice(detail: StockDetail, timeSeriesData: TimeSeriesData): StockDetail {
        val latestData = timeSeriesData.dailyData.firstOrNull()
        return if (latestData != null) {
            val previousData = timeSeriesData.dailyData.getOrNull(1)
            val change = if (previousData != null) {
                latestData.close - previousData.close
            } else 0.0
            val changePercent = if (previousData != null && previousData.close != 0.0) {
                String.format("%.2f%%", (change / previousData.close) * 100)
            } else "0.00%"

            detail.copy(
                currentPrice = latestData.close,
                change = change,
                changePercent = changePercent,
                volume = latestData.volume
            )
        } else detail
    }

}