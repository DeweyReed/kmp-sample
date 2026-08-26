package com.github.deweyreed.souvenir

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
internal sealed interface AppRoute : NavKey {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Detail(val id: Long) : AppRoute

    @Serializable
    data object Settings : AppRoute
}

internal class AppNavigator(private val backStack: MutableList<AppRoute>) {
    fun navigate(route: AppRoute, singleTop: Boolean = false) {
        if (singleTop && backStack.lastOrNull() == route) {
            return
        }
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}
