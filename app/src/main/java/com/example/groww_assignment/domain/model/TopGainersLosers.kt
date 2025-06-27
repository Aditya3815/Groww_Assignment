package com.example.groww_assignment.domain.model

data class TopGainersLosers(
    val lastUpdated: String,
    val topGainers: List<Stock>,
    val topLosers: List<Stock>,
    val mostActivelyTraded: List<Stock>
)
