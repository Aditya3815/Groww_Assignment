package com.example.groww_assignment.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResultDto(
    @SerializedName("bestMatches")
    val bestMatches: List<SearchMatchDto>?
)
