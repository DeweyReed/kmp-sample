@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.home.presentation

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
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import souvenir.feature.home.presentation.generated.resources.Res
import souvenir.feature.home.presentation.generated.resources.feature_home_back

@Composable
fun Detail(
    id: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<DetailViewModel>()
    LaunchedEffect(id) { viewModel.load(id) }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Screen(
        screen = screen,
        onBack = onBack,
        modifier = modifier,
    )
}

@Composable
private fun Screen(
    screen: DetailViewModel.Screen,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val article = screen.article
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = article?.title ?: "")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(Res.drawable.feature_home_back),
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
                .padding(it)
                .padding(16.dp),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(article?.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = article?.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = article?.summary ?: "",
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
