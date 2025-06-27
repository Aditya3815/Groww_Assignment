package com.example.groww_assignment.presentation.navigation

sealed class Screen(val route: String) {
    object Explore : Screen("explore")
    object Watchlist : Screen("watchlist")
    object StockDetail : Screen("stock_detail/{symbol}") {
        fun createRoute(symbol: String) = "stock_detail/$symbol"
    }
    object ViewAll : Screen("view_all/{type}") {
        fun createRoute(type: String) = "view_all/$type"
    }
}