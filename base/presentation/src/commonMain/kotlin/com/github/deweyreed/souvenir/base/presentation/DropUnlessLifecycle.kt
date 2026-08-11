package com.github.deweyreed.souvenir.base.presentation

import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun <T> dropUnlessResumed(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    block: (T) -> Unit,
): (T) -> Unit = { value ->
    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        block(value)
    }
}
