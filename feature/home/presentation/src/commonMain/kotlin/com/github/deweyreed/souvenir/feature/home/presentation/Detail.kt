@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.zacsweers.metrox.viewmodel.metroViewModel
import org.jetbrains.compose.resources.painterResource
import souvenir.base.presentation.generated.resources.back
import souvenir.base.presentation.generated.resources.Res as ResBase

@Composable
fun Detail(
    id: Long,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
) {
    val viewModel = metroViewModel<DetailViewModel>()
    LaunchedEffect(id) { viewModel.load(id) }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Screen(
        id = id,
        screen = screen,
        onBack = onBack,
        sharedTransitionScope = sharedTransitionScope,
        animatedContentScope = animatedContentScope,
        modifier = modifier,
    )
}

@Composable
private fun Screen(
    id: Long,
    screen: DetailViewModel.Screen,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
) {
    val article = screen.article
    Scaffold(
        modifier = modifier,
        topBar = {
            sharedTransitionScope.run {
                animatedContentScope.run {
                    TopAppBar(
                        title = {
                            Text(text = article?.title ?: "")
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                    painter = painterResource(ResBase.drawable.back),
                                    contentDescription = "Back",
                                )
                            }
                        },
                        modifier = Modifier
                            .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f)
                            .animateEnterExit(enter = fadeIn(), exit = fadeOut()),
                    )
                }
            }
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
        ) {
            sharedTransitionScope.run {
                val imageUrl = article?.imageUrl
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(imageUrl)
                        .crossfade(true)
                        .placeholderMemoryCacheKey(imageUrl)
                        .memoryCacheKey(imageUrl)
                        .build(),
                    contentDescription = article?.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .sharedElement(
                            sharedContentState = sharedTransitionScope
                                .rememberSharedContentState(id),
                            animatedVisibilityScope = animatedContentScope,
                        )
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article?.summary ?: "",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
