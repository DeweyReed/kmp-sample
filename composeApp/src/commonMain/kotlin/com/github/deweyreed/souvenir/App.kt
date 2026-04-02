package com.github.deweyreed.souvenir

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.deweyreed.souvenir.feature.home.presentation.Detail
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.settings.presentation.Settings
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import kotlinx.serialization.Serializable

private val graph by lazy { createGraph<AppGraph>() }

@Composable
fun App() {
    CompositionLocalProvider(LocalMetroViewModelFactory provides graph.metroViewModelFactory) {
        val navController = rememberNavController()
        MaterialTheme {
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = Destination.Home,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    composable<Destination.Home> {
                        Home(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable,
                            onDetailClick = { navController.navigate(Destination.Detail(it)) },
                            onSettingsClick = dropUnlessStarted {
                                navController.navigate(Destination.Settings)
                            },
                        )
                    }
                    composable<Destination.Detail> {
                        val route = it.toRoute<Destination.Detail>()
                        Detail(
                            id = route.id,
                            onBack = dropUnlessStarted(block = navController::popBackStack),
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = this@composable,
                        )
                    }
                    composable<Destination.Settings> {
                        Settings(
                            onBack = dropUnlessStarted(block = navController::popBackStack),
                            sharedTransitionScope = this@SharedTransitionLayout,
                        )
                    }
                }
            }
        }
    }
}

sealed interface Destination {
    @Serializable
    data object Home : Destination

    @Serializable
    data class Detail(val id: Long) : Destination

    @Serializable
    data object Settings : Destination
}
