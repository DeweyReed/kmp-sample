package com.github.deweyreed.souvenir

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.deweyreed.souvenir.data.dataModule
import com.github.deweyreed.souvenir.feature.home.presentation.Home
import com.github.deweyreed.souvenir.feature.home.presentation.homeModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplication

@Composable
@Preview
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
            ) {
                Home()
            }
        }
    }
}
