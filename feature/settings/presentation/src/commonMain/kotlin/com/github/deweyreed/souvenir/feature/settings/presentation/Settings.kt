@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import souvenir.base.presentation.generated.resources.back
import souvenir.feature.settings.presentation.generated.resources.Res
import souvenir.base.presentation.generated.resources.Res as ResBase

@Composable
fun Settings(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    val controller = rememberNavController()
    Scaffold(
        modifier = modifier,
        topBar = {
            sharedTransitionScope.run {
                TopAppBar(
                    title = {
                        val destination =
                            controller.currentBackStackEntryAsState().value?.destination
                        Text(
                            when {
                                destination?.hasRoute<Destination.Libraries>() == true -> {
                                    "Libraries"
                                }
                                else -> "Settings"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (!controller.popBackStack()) {
                                    onBack()
                                }
                            },
                        ) {
                            Icon(
                                painter = painterResource(ResBase.drawable.back),
                                contentDescription = "Back",
                            )
                        }
                    },
                    modifier = Modifier.renderInSharedTransitionScopeOverlay(
                        zIndexInOverlay = 1f,
                    ),
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = controller,
            startDestination = Destination.Settings,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<Destination.Settings> {
                SettingsScreen(
                    onLibraries = { controller.navigate(Destination.Libraries) },
                    contentPadding = padding,
                )
            }
            composable<Destination.Libraries> {
                val libraries by produceLibraries {
                    Res.readBytes("files/aboutLibraries.json").decodeToString()
                }
                LibrariesContainer(
                    libraries = libraries,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = padding,
                )
            }
        }
    }
}

private sealed interface Destination {
    @Serializable
    data object Settings : Destination

    @Serializable
    data object Libraries : Destination
}
