package com.example.groww_assignment.presentation.navigation

import androidx.navigation.NavController

fun NavController.navigateToStockDetail(symbol: String) {
    navigate(Screen.StockDetail.createRoute(symbol))
}

fun NavController.navigateToViewAll(type: String) {
    navigate(Screen.ViewAll.createRoute(type))
}

fun NavController.popBackStackSafely(): Boolean {
    return if (previousBackStackEntry != null) {
        popBackStack()
    } else {
        false
    }
}