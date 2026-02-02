package com.github.deweyreed.souvenir

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessStarted
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.github.deweyreed.souvenir.base.api.dispatchersModule
import com.github.deweyreed.souvenir.data.dataModule
import com.github.deweyreed.souvenir.feature.home.presentation.Detail
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.home.presentation.homeModule
import kotlinx.serialization.Serializable
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(
                dispatchersModule,
                dataModule,
                homeModule,
                appModule,
            )
        },
    ) {
        MaterialTheme {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = Destination.Home,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable<Destination.Home> {
                    Home(
                        onDetailClick = { navController.navigate(Destination.Detail(it)) },
                    )
                }
                composable<Destination.Detail> {
                    val route = it.toRoute<Destination.Detail>()
                    Detail(
                        id = route.id,
                        onBack = dropUnlessStarted(block = navController::popBackStack),
                    )
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
}
