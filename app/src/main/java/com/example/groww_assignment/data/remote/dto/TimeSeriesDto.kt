package com.example.groww_assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TimeSeriesDto(
    @SerializedName("Meta Data")
    val metaData: MetaDataDto?,
    @SerializedName("Time Series (Daily)")
    val timeSeries: Map<String, DailyDataDto>?
)