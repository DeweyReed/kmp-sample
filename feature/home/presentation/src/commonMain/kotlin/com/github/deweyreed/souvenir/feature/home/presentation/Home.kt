package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Home(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeViewModel>()
    LaunchedEffect(Unit) { viewModel.load() }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Box(modifier = modifier.fillMaxSize()) {
        val articles = screen.articles
        if (articles != null) {
            LazyColumn(modifier = Modifier.matchParentSize()) {
                items(items = articles, key = { it.id }) { item ->
                    ListItem(
                        headlineContent = { Text(text = item.title) },
                    )
                }
            }
        } else {
            Box(modifier = Modifier.matchParentSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
