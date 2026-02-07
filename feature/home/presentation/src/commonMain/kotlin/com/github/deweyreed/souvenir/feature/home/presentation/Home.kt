@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import souvenir.feature.home.presentation.generated.resources.Res
import souvenir.feature.home.presentation.generated.resources.feature_home_open_in_new

@Composable
fun Home(
    onDetailClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    LaunchedEffect(Unit) { viewModel.load() }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Screen(
        screen = screen,
        onAction = viewModel::onAction,
        onDetailClick = onDetailClick,
        modifier = modifier,
    )
}

@Composable
private fun Screen(
    screen: HomeViewModel.Screen,
    onAction: (HomeViewModel.Action) -> Unit,
    onDetailClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Spaceflight News") },
                actions = {
                    val uriHandler = LocalUriHandler.current
                    IconButton(
                        onClick = { uriHandler.openUri("https://spaceflightnewsapi.net/") },
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.feature_home_open_in_new),
                            contentDescription = null,
                        )
                    }
                }
            )
        },
    ) { padding ->
        val items = screen.articles
        if (items != null) {
            ArticleList(
                items = items,
                onItemClick = { onDetailClick(it.id) },
                onLoadMore = { onAction(HomeViewModel.Action.LoadMoreItems) },
                modifier = Modifier.fillMaxSize(),
                padding = padding,
            )
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun ArticleList(
    items: List<ArticleEntity>,
    onItemClick: (ArticleEntity) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(0.dp),
) {
    val state = rememberLazyStaggeredGridState()

    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val firstVisibleItemIndex = state.firstVisibleItemIndex
    LaunchedEffect(items.size, firstVisibleItemIndex) {
        if (items.size - firstVisibleItemIndex >= 10) return@LaunchedEffect
        currentOnLoadMore()
    }

    val layoutDirection = LocalLayoutDirection.current
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(240.dp),
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(
            start = padding.calculateStartPadding(layoutDirection) + 16.dp,
            top = padding.calculateTopPadding() + 16.dp,
            end = padding.calculateEndPadding(layoutDirection) + 16.dp,
            bottom = padding.calculateBottomPadding() + 16.dp
        ),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items = items, key = { it.id }) { item ->
            ArticleItem(
                title = item.title,
                imageUrl = item.imageUrl,
                summary = item.summary,
                modifier = Modifier
                    .animateItem()
                    .clickable { onItemClick(item) },
            )
        }
    }
}

@Composable
private fun ArticleItem(
    title: String,
    imageUrl: String?,
    summary: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalPlatformContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = summary.trim(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 10,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
