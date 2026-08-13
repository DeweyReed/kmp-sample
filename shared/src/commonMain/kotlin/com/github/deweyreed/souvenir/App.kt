package com.github.deweyreed.souvenir

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.base.presentation.dropUnlessResumed
import com.github.deweyreed.souvenir.feature.home.presentation.Detail
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.settings.presentation.Settings
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable

private val appGraph by lazy { createGraph<AppGraph>() }

@Composable
fun App() {
    CompositionLocalProvider(
        LocalMetroViewModelFactory provides appGraph.metroViewModelFactory,
    ) {
        AppRoot()
    }
}

@Composable
private fun AppRoot(
    viewModel: AppViewModel = metroViewModel(),
) {
    LaunchedEffect(viewModel) { viewModel.load() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    AppUi(uiState = uiState)
}

@Composable
private fun AppUi(
    uiState: AppUiState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    AppMaterialTheme(theme = uiState.theme) {
        SharedTransitionLayout {
            AppNavHost(
                navController = navController,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun AppMaterialTheme(
    theme: AppTheme,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (theme) {
        AppTheme.SYSTEM -> isSystemInDarkTheme()
        AppTheme.LIGHT -> false
        AppTheme.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}

private val LightColorScheme = lightColorScheme()
private val DarkColorScheme = darkColorScheme()

@Serializable
private sealed interface AppRoute {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Detail(val id: Long) : AppRoute

    @Serializable
    data object Settings : AppRoute
}

@Composable
private fun SharedTransitionScope.AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Home,
        modifier = modifier.fillMaxSize(),
    ) {
        composable<AppRoute.Home> {
            Home(
                onDetailClick = dropUnlessResumed { articleId ->
                    navController.navigate(AppRoute.Detail(articleId)) {
                        launchSingleTop = true
                    }
                },
                onSettingsClick = dropUnlessResumed {
                    navController.navigate(AppRoute.Settings) {
                        launchSingleTop = true
                    }
                },
                sharedTransitionScope = this@AppNavHost,
                animatedContentScope = this,
            )
        }
        composable<AppRoute.Detail> { backStackEntry ->
            val route = backStackEntry.toRoute<AppRoute.Detail>()
            Detail(
                id = route.id,
                onBack = dropUnlessResumed { navController.popBackStack() },
                sharedTransitionScope = this@AppNavHost,
                animatedContentScope = this,
            )
        }
        composable<AppRoute.Settings> {
            Settings(
                onBack = dropUnlessResumed { navController.popBackStack() },
                sharedTransitionScope = this@AppNavHost,
            )
        }
    }
}
