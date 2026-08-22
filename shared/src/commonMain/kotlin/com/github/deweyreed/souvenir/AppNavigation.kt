package com.github.deweyreed.souvenir

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
internal sealed interface AppRoute : NavKey {
    @Serializable
    data object Home : AppRoute

    @Serializable
    data class Detail(val id: Long) : AppRoute

    @Serializable
    data object Settings : AppRoute
}

internal val AppRouteSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(AppRoute.Home::class, AppRoute.Home.serializer())
        subclass(AppRoute.Detail::class, AppRoute.Detail.serializer())
        subclass(AppRoute.Settings::class, AppRoute.Settings.serializer())
    }
}

internal val AppNavigationSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = AppRouteSerializersModule
}

internal class AppNavigator(
    private val backStack: MutableList<NavKey>,
) {
    fun navigate(route: AppRoute, singleTop: Boolean = false) {
        if (singleTop && backStack.lastOrNull() == route) {
            return
        }
        backStack.add(route)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }
}
