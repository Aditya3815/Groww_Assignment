package com.example.groww_assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TopGainersLosersDto(
    @SerializedName("metadata")
    val metadata: String?,
    @SerializedName("last_updated")
    val lastUpdated: String?,
    @SerializedName("top_gainers")
    val topGainers: List<StockItemDto>?,
    @SerializedName("top_losers")
    val topLosers: List<StockItemDto>?,
    @SerializedName("most_actively_traded")
    val mostActivelyTraded: List<StockItemDto>?
)
