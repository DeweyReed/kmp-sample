package com.github.deweyreed.souvenir

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class AppNavigatorTest {
    @Test
    fun `navigate appends destination`() {
        val backStack = mutableListOf<NavKey>(AppRoute.Home)
        val navigator = AppNavigator(backStack)

        navigator.navigate(AppRoute.Detail(id = 42L))

        assertEquals(
            listOf<NavKey>(AppRoute.Home, AppRoute.Detail(id = 42L)),
            backStack
        )
    }

    @Test
    fun `navigate appends current destination again by default`() {
        val route = AppRoute.Detail(id = 42L)
        val backStack = mutableListOf<NavKey>(AppRoute.Home, route)
        val navigator = AppNavigator(backStack)

        navigator.navigate(route)

        assertEquals(listOf<NavKey>(AppRoute.Home, route, route), backStack)
    }

    @Test
    fun `navigate skips current destination when singleTop is enabled`() {
        val route = AppRoute.Detail(id = 42L)
        val backStack =
            mutableListOf<NavKey>(AppRoute.Home, route)
        val navigator = AppNavigator(backStack)

        navigator.navigate(route, singleTop = true)

        assertEquals(listOf<NavKey>(AppRoute.Home, route), backStack)
    }

    @Test
    fun `navigate appends destination found deeper in the back stack when singleTop is enabled`() {
        val backStack = mutableListOf<NavKey>(
            AppRoute.Home,
            AppRoute.Detail(id = 42L),
            AppRoute.Settings,
        )
        val navigator = AppNavigator(backStack)

        navigator.navigate(AppRoute.Detail(id = 42L), singleTop = true)

        assertEquals(
            listOf<NavKey>(
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
        val backStack = mutableListOf<NavKey>(
            AppRoute.Home,
            AppRoute.Settings,
        )
        val navigator = AppNavigator(backStack)

        navigator.goBack()

        assertEquals(listOf<NavKey>(AppRoute.Home), backStack)
    }

    @Test
    fun `goBack keeps the last remaining destination`() {
        val backStack = mutableListOf<NavKey>(AppRoute.Home)
        val navigator = AppNavigator(backStack)

        navigator.goBack()

        assertEquals(listOf<NavKey>(AppRoute.Home), backStack)
    }
}

class AppRouteSerializationTest {
    private val json = Json {
        serializersModule = AppRouteSerializersModule
    }
    private val serializer = PolymorphicSerializer(NavKey::class)

    @Test
    fun `routes round trip through NavKey serialization`() {
        val routes = listOf<NavKey>(
            AppRoute.Home,
            AppRoute.Detail(id = Long.MAX_VALUE),
            AppRoute.Settings,
        )

        routes.forEach { route ->
            val encoded = json.encodeToString(serializer, route)

            assertEquals(
                route,
                json.decodeFromString(serializer, encoded)
            )
        }
    }
}
