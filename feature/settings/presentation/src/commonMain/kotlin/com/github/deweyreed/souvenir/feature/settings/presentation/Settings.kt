@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.settings.presentation

import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import souvenir.base.presentation.generated.resources.back
import souvenir.base.presentation.generated.resources.Res as ResBase

@Composable
fun Settings(
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            sharedTransitionScope.run {
                TopAppBar(
                    title = {
                        Text("Settings")
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
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
    ) {

    }
}
