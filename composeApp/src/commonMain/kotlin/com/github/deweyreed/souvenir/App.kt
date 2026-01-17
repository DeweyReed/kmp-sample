package com.github.deweyreed.souvenir

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.deweyreed.souvenir.data.dataModule
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.home.presentation.homeModule
import kotlinx.serialization.Serializable
import org.koin.compose.KoinApplication

@Composable
fun App() {
    KoinApplication(
        application = {
            modules(
                dataModule,
                homeModule,
                appModule,
            )
        },
    ) {
        MaterialTheme {
            NavHost(
                navController = rememberNavController(),
                startDestination = Home,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable<Home> {
                    Home()
                }
            }
        }
    }
}

@Serializable
object Home
