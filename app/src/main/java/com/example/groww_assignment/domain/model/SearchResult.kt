package com.example.groww_assignment.domain.model

data class SearchResult(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val marketOpen: String,
    val marketClose: String,
    val timezone: String,
    val currency: String,
    val matchScore: Double
)
