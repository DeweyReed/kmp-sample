package com.github.deweyreed.souvenir

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.savedstate.serialization.decodeFromSavedState
import androidx.savedstate.serialization.encodeToSavedState
import kotlin.test.Test
import kotlin.test.assertEquals

class AppNavigatorTest {
    @Test
    fun `navigate appends destination`() {
        val backStack = mutableListOf<AppRoute>(AppRoute.Home)
        val navigator = AppNavigator(backStack)

        navigator.navigate(AppRoute.Detail(id = 42L))

        assertEquals(
            listOf(AppRoute.Home, AppRoute.Detail(id = 42L)),
            backStack
        )
    }

    @Test
    fun `navigate appends current destination again by default`() {
        val route = AppRoute.Detail(id = 42L)
        val backStack = mutableListOf(AppRoute.Home, route)
        val navigator = AppNavigator(backStack)

        navigator.navigate(route)

        assertEquals(listOf(AppRoute.Home, route, route), backStack)
    }

    @Test
    fun `navigate skips current destination when singleTop is enabled`() {
        val route = AppRoute.Detail(id = 42L)
        val backStack =
            mutableListOf(AppRoute.Home, route)
        val navigator = AppNavigator(backStack)

        navigator.navigate(route, singleTop = true)

        assertEquals(listOf(AppRoute.Home, route), backStack)
    }

    @Test
    fun `navigate appends destination found deeper in the back stack when singleTop is enabled`() {
        val backStack = mutableListOf(
            AppRoute.Home,
            AppRoute.Detail(id = 42L),
            AppRoute.Settings,
        )
        val navigator = AppNavigator(backStack)

        navigator.navigate(AppRoute.Detail(id = 42L), singleTop = true)

        assertEquals(
            listOf(
                AppRoute.Home,
                AppRoute.Detail(id = 42L),
                AppRoute.Settings,
                AppRoute.Detail(id = 42L),
            ),
            backStack,
        )
    }

    @Test
    fun `goBack removes current destination`() {
        // <AppRoute> looks redundant on JVM but is required on Kotlin/Native, where literals of
        // @Serializable objects infer MutableList<AppRoute & SerializerFactory>.
        @Suppress("RemoveExplicitTypeArguments")
        val backStack = mutableListOf<AppRoute>(
            AppRoute.Home,
            AppRoute.Settings,
        )
        val navigator = AppNavigator(backStack)

        navigator.goBack()

        assertEquals(listOf<AppRoute>(AppRoute.Home), backStack)
    }

    @Test
    fun `goBack keeps the last remaining destination`() {
        val backStack = mutableListOf<AppRoute>(AppRoute.Home)
        val navigator = AppNavigator(backStack)

        navigator.goBack()

        assertEquals(listOf<AppRoute>(AppRoute.Home), backStack)
    }
}

class AppRouteSerializationTest {
    @Test
    fun `back stack round trips through saved state`() {
        val serializer = NavBackStackSerializer<AppRoute>()
        val backStack = NavBackStack(
            AppRoute.Home,
            AppRoute.Detail(id = Long.MAX_VALUE),
            AppRoute.Settings,
        )

        val encoded = encodeToSavedState(serializer, backStack)

        assertEquals(
            backStack.toList(),
            decodeFromSavedState(serializer, encoded).toList(),
        )
    }
}
