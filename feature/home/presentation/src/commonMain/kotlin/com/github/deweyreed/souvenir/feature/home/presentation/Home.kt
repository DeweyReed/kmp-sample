package com.github.deweyreed.souvenir.feature.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Home(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<HomeViewModel>()
    LaunchedEffect(Unit) { viewModel.load() }
    val screen by viewModel.screen.collectAsStateWithLifecycle()
    Column(modifier = modifier.fillMaxSize()) {
        screen.articles?.forEach {
            ListItem(
                headlineContent = { Text(text = it.title) },
            )
        }
    }
}
