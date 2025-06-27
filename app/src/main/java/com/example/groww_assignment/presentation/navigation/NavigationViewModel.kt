package com.example.groww_assignment.presentation.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor() : ViewModel() {

    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    private val _bottomBarVisible = MutableStateFlow(true)
    val bottomBarVisible: StateFlow<Boolean> = _bottomBarVisible.asStateFlow()

    fun updateCurrentRoute(route: String?) {
        _currentRoute.value = route
        updateBottomBarVisibility(route)
    }

    private fun updateBottomBarVisibility(route: String?) {
        _bottomBarVisible.value = when {
            route == null -> true
            route.startsWith(Screen.Explore.route) -> true
            route.startsWith(Screen.Watchlist.route) -> true
            else -> false
        }
    }

}