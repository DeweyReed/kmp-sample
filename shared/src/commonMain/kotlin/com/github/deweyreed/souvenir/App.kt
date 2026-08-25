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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.feature.home.presentation.Detail
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.settings.presentation.Settings
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.metroViewModel

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
    AppMaterialTheme(theme = uiState.theme) {
        SharedTransitionLayout {
            AppNavDisplay(modifier = modifier)
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

@Composable
private fun SharedTransitionScope.AppNavDisplay(
    modifier: Modifier = Modifier,
) {
    val backStack = rememberSerializable(
        serializer = NavBackStackSerializer<AppRoute>(),
    ) {
        NavBackStack(AppRoute.Home)
    }
    val navigator = remember(backStack) { AppNavigator(backStack) }

    NavDisplay(
        backStack = backStack,
        modifier = modifier.fillMaxSize(),
        onBack = navigator::goBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<AppRoute.Home> {
                Home(
                    onDetailClick = { articleId ->
                        navigator.navigate(route = AppRoute.Detail(articleId), singleTop = true)
                    },
                    onSettingsClick = {
                        navigator.navigate(AppRoute.Settings, singleTop = true)
                    },
                    sharedTransitionScope = this@AppNavDisplay,
                    animatedContentScope = LocalNavAnimatedContentScope.current,
                )
            }
            entry<AppRoute.Detail> { route ->
                Detail(
                    id = route.id,
                    onBack = navigator::goBack,
                    sharedTransitionScope = this@AppNavDisplay,
                    animatedContentScope = LocalNavAnimatedContentScope.current,
                )
            }
            entry<AppRoute.Settings> {
                Settings(
                    onBack = navigator::goBack,
                    sharedTransitionScope = this@AppNavDisplay,
                )
            }
        },
    )
}
