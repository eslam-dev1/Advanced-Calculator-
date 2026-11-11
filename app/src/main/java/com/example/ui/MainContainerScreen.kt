package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.BaseConverterScreen
import com.example.ui.screens.GrapherScreen
import com.example.ui.screens.UnitConverterScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.SmartCalculatorTheme

/**
 * MainContainerScreen - Refactored for industry-standard Navigation Compose.
 * Optimized for performance (RAM/Smoothness) by properly managing screen lifecycles.
 */
@Composable
fun MainContainerScreen(
    viewModel: CalculatorViewModel = viewModel()
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route
    val activeAccentColor = viewModel.availableAccentColors.getOrElse(uiState.accentColorIndex) { NeonCyan }

    val items = listOf(
        NavigationItem(CalculatorTab.CALCULATOR, Icons.Filled.Calculate, Icons.Outlined.Calculate, "calc"),
        NavigationItem(CalculatorTab.CONVERTER, Icons.Filled.SwapHoriz, Icons.Outlined.SwapHoriz, "conv"),
        NavigationItem(CalculatorTab.PROGRAMMER, Icons.Filled.Code, Icons.Outlined.Code, "prog"),
        NavigationItem(CalculatorTab.GRAPHER, Icons.Filled.Timeline, Icons.Outlined.Timeline, "graph")
    )

    BackHandler(enabled = currentRoute != "calc" && !uiState.isInPipMode) {
        viewModel.onSelectTab(CalculatorTab.CALCULATOR)
        navController.navigate("calc") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    SmartCalculatorTheme(
        accentColor = activeAccentColor,
        backgroundTheme = uiState.backgroundTheme,
        isPowerSaving = uiState.isPowerSavingActive,
        isSoundEnabled = uiState.isSoundEnabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        viewModel.onUserInteraction()
                    }
                }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    if (!uiState.isInPipMode) {
                        NavigationBar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .testTag("main_bottom_nav"),
                            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            items.forEach { item ->
                                val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = {
                                        if (item.tab != uiState.currentTab) {
                                            viewModel.onSelectTab(item.tab)
                                            navController.navigate(item.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = androidx.compose.ui.res.stringResource(item.tab.titleResId)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = androidx.compose.ui.res.stringResource(item.tab.titleResId),
                                            fontSize = 10.sp,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = if (uiState.isPowerSavingActive) androidx.compose.material3.MaterialTheme.colorScheme.background.copy(alpha = 0.5f) else androidx.compose.material3.MaterialTheme.colorScheme.background,
                                        selectedTextColor = if (uiState.isPowerSavingActive) activeAccentColor.copy(alpha = 0.5f) else activeAccentColor,
                                        indicatorColor = if (uiState.isPowerSavingActive) activeAccentColor.copy(alpha = 0.4f) else activeAccentColor,
                                        unselectedIconColor = DisplayFormulaColor.copy(alpha = 0.4f),
                                        unselectedTextColor = DisplayFormulaColor.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("nav_tab_${item.route}")
                                )
                            }
                        }
                    }
                },
                containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(if (uiState.isInPipMode) Modifier else Modifier.padding(innerPadding))
                        .then(if (uiState.isInPipMode) Modifier else Modifier.statusBarsPadding())
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "calc",
                        enterTransition = { if (uiState.isPowerSavingActive) EnterTransition.None else fadeIn(tween(200)) },
                        exitTransition = { if (uiState.isPowerSavingActive) ExitTransition.None else fadeOut(tween(200)) },
                        popEnterTransition = { if (uiState.isPowerSavingActive) EnterTransition.None else fadeIn(tween(200)) },
                        popExitTransition = { if (uiState.isPowerSavingActive) ExitTransition.None else fadeOut(tween(200)) }
                    ) {
                        composable("calc") {
                            CalculatorScreen(viewModel = viewModel)
                        }
                        composable("conv") {
                            UnitConverterScreen(
                                uiState = uiState,
                                onSelectCategory = { viewModel.onSelectUnitCategory(it) },
                                onSelectFromUnit = { viewModel.onSelectFromUnit(it) },
                                onSelectToUnit = { viewModel.onSelectToUnit(it) },
                                onSwapUnits = { viewModel.onSwapUnits() },
                                onInputDigit = { viewModel.onUnitInput(it) },
                                onBackspace = { viewModel.onUnitBackspace() },
                                onClear = { viewModel.onUnitClear() },
                                onToast = { viewModel.setToastMessage(it) },
                                onRefreshCurrencyRates = { viewModel.fetchCurrencyRates() }
                            )
                        }
                        composable("prog") {
                            BaseConverterScreen(
                                uiState = uiState,
                                onSelectBase = { viewModel.onSelectProgrammerBase(it) },
                                onInputChar = { viewModel.onProgrammerInput(it) },
                                onBackspace = { viewModel.onProgrammerBackspace() },
                                onClear = { viewModel.onProgrammerClear() },
                                onToast = { viewModel.setToastMessage(it) }
                            )
                        }
                        composable("graph") {
                            GrapherScreen(
                                uiState = uiState,
                                onUpdateFunction = { viewModel.onUpdateGraphFunction(it) },
                                onZoom = { viewModel.onZoomGraph(it) }
                            )
                        }
                    }
                }
            }

            // Floating Mini Calculator Overlay (stays on top of screens for quick calculations)
            if (!uiState.isInPipMode) {
                com.example.ui.components.MiniCalculatorOverlay(
                    uiState = uiState,
                    onDigit = { viewModel.onDigit(it) },
                    onOperator = { viewModel.onOperator(it) },
                    onDecimalPoint = { viewModel.onDecimalPoint() },
                    onClear = { viewModel.onClear() },
                    onBackspace = { viewModel.onBackspace() },
                    onEquals = { viewModel.onEquals() },
                    onClose = { viewModel.onToggleMiniOverlayMode(false) },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Power Saving Dimming Overlay (Covers the whole screen including bottom bar)
            if (uiState.isPowerSavingActive && !uiState.isInPipMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.55f))
                        .testTag("power_saving_overlay"),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = androidx.compose.ui.res.stringResource(com.example.R.string.power_saving_mode),
                        color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            }
        }
    }
}

private data class NavigationItem(
    val tab: CalculatorTab,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
