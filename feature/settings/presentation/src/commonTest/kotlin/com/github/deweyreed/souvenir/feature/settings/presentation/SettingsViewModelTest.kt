package com.github.deweyreed.souvenir.feature.settings.presentation

import com.github.deweyreed.souvenir.base.api.AppTheme
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.base.api.setAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var settings: FakeSettings
    private lateinit var viewModel: SettingsViewModel

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        settings = FakeSettings()
        viewModel = SettingsViewModel(
            ioDispatcher = testDispatcher,
            settings = settings,
        )
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial theme should be system`() = runTest {
        viewModel.load()
        advanceUntilIdle()
        assertEquals(AppTheme.SYSTEM, viewModel.uiState.value.theme)
    }

    @Test
    fun `theme should update when settings change`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        settings.setAppTheme(AppTheme.DARK)
        advanceUntilIdle()
        assertEquals(AppTheme.DARK, viewModel.uiState.value.theme)

        settings.setAppTheme(AppTheme.LIGHT)
        advanceUntilIdle()
        assertEquals(AppTheme.LIGHT, viewModel.uiState.value.theme)
    }

    @Test
    fun `onAction SetTheme should update settings`() = runTest {
        viewModel.load()
        advanceUntilIdle()

        viewModel.onAction(SettingsAction.SetTheme(AppTheme.DARK))
        advanceUntilIdle()

        assertEquals(AppTheme.DARK.name, settings.themeName)
    }

    private class FakeSettings : Settings {
        private val strings = MutableStateFlow<Map<String, String?>>(emptyMap())

        val themeName: String? get() = strings.value["theme"]

        override fun getBooleanFlow(key: String): Flow<Boolean?> = error("Not used")
        override suspend fun setBoolean(key: String, value: Boolean?) = error("Not used")

        override fun getStringFlow(key: String): Flow<String?> = strings.map { it[key] }
        override suspend fun setString(key: String, value: String?) {
            strings.update { it + (key to value) }
        }
    }
}
