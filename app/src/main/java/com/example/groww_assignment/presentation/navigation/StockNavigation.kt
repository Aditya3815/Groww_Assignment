package com.example.groww_assignment.presentation.navigation


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groww_assignment.presentation.screens.explore.ExploreScreen
import com.example.groww_assignment.presentation.screens.stock_detail.StockDetailScreen
import com.example.groww_assignment.presentation.screens.view_all.ViewAllScreen
import com.example.groww_assignment.presentation.screens.watchlist.WatchlistScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StocksNavigation(
    navigationViewModel: NavigationViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val bottomBarVisible by navigationViewModel.bottomBarVisible.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        navigationViewModel.updateCurrentRoute(currentRoute)
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                StocksBottomBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Explore.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }) + fadeIn()
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
            }
        ) {
            composable(Screen.Explore.route) {
                ExploreScreen(
                    onStockClick = { symbol ->
                        navController.navigateToStockDetail(symbol)
                    },
                    onViewAllClick = { type ->
                        navController.navigateToViewAll(type)
                    }
                )
            }

            composable(Screen.Watchlist.route) {
                WatchlistScreen(
                    onStockClick = { symbol ->
                        navController.navigateToStockDetail(symbol)
                    }
                )
            }

            composable(
                route = Screen.StockDetail.route,
                enterTransition = {
                    slideInVertically(initialOffsetY = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutVertically(targetOffsetY = { it }) + fadeOut()
                }
            ) { backStackEntry ->
                val symbol = backStackEntry.arguments?.getString("symbol") ?: return@composable
                StockDetailScreen(
                    symbol = symbol,
                    onBackClick = { navController.popBackStackSafely() }
                )
            }

            composable(
                route = Screen.ViewAll.route,
                enterTransition = {
                    slideInHorizontally(initialOffsetX = { it }) + fadeIn()
                },
                exitTransition = {
                    slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
                }
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: return@composable
                ViewAllScreen(
                    type = type,
                    onBackClick = { navController.popBackStackSafely() },
                    onStockClick = { symbol ->
                        navController.navigateToStockDetail(symbol)
                    }
                )
            }
        }
    }
}

@Composable
private fun StocksBottomBar(
    navController: NavController,
    currentRoute: String?
) {
    val items = listOf(
        BottomNavItem(
            title = "Explore",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            screen = Screen.Explore
        ),
        BottomNavItem(
            title = "Watchlist",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.StarBorder,
            screen = Screen.Watchlist
        )
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.screen.route
            } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

data class BottomNavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val screen: Screen
)
