package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Home(
    onDetailClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<HomeViewModel>()
    LaunchedEffect(Unit) { viewModel.load() }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Box(modifier = modifier.fillMaxSize()) {
        val items = screen.articles
        if (items != null) {
            ArticleList(
                items = items,
                onItemClick = { onDetailClick(it.id) },
                onLoadMore = { viewModel.onAction(HomeViewModel.Action.LoadMoreItems) },
                modifier = Modifier.matchParentSize(),
            )
        } else {
            Box(modifier = Modifier.matchParentSize()) {
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
) {
    val state = rememberLazyStaggeredGridState()

    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val firstVisibleItemIndex = state.firstVisibleItemIndex
    LaunchedEffect(items.size, firstVisibleItemIndex) {
        if (items.size - firstVisibleItemIndex >= 10) return@LaunchedEffect
        currentOnLoadMore()
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(240.dp),
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        ),
        state = state,
        contentPadding = PaddingValues(horizontal = 16.dp),
        verticalItemSpacing = 16.dp,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .windowInsetsBottomHeight(
                        WindowInsets.systemBars.only(WindowInsetsSides.Top)
                    ),
            )
        }
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
        item(span = StaggeredGridItemSpan.FullLine) {
            Spacer(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .windowInsetsBottomHeight(
                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                    ),
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
