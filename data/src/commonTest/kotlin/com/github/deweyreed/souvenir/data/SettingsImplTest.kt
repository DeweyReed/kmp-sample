package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsImplTest {
    @Test
    fun `setString completes when the calling scope is cancelled mid-write`() = runTest {
        val dataStore = FakeDataStore(writeDelay = WRITE_DELAY)
        val applicationScope = testScope()
        val settings = SettingsImpl(dataStore = dataStore, applicationScope = applicationScope)

        val callerScope = CoroutineScope(StandardTestDispatcher(testScheduler))
        callerScope.launch { settings.setString(KEY, VALUE) }

        advanceTimeBy(WRITE_DELAY / 2)
        assertNull(dataStore.readString(KEY), "the write should still be in flight")

        callerScope.cancel()
        advanceUntilIdle()

        assertEquals(VALUE, dataStore.readString(KEY))
        applicationScope.cancel()
    }

    @Test
    fun `setString suspends until the write completes`() = runTest {
        val dataStore = FakeDataStore(writeDelay = WRITE_DELAY)
        val applicationScope = testScope()
        val settings = SettingsImpl(dataStore, applicationScope)

        var returned = false
        launch {
            settings.setString(KEY, VALUE)
            returned = true
        }

        advanceTimeBy(WRITE_DELAY / 2)
        assertFalse(returned, "the caller should still be suspended")

        advanceUntilIdle()
        assertTrue(returned)
        assertEquals(VALUE, dataStore.readString(KEY))
        applicationScope.cancel()
    }

    @Test
    fun `values round trip and null removes the key`() = runTest {
        val dataStore = FakeDataStore()
        val applicationScope = testScope()
        val settings = SettingsImpl(dataStore, applicationScope)

        settings.setString(KEY, VALUE)
        settings.setBoolean(BOOLEAN_KEY, true)
        assertEquals(VALUE, settings.getString(KEY))
        assertEquals(true, settings.getBoolean(BOOLEAN_KEY))

        settings.setString(KEY, null)
        settings.setBoolean(BOOLEAN_KEY, null)
        assertNull(settings.getString(KEY))
        assertNull(settings.getBoolean(BOOLEAN_KEY))

        applicationScope.cancel()
    }

    private fun TestScope.testScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + StandardTestDispatcher(testScheduler))
    }

    private class FakeDataStore(
        private val writeDelay: Duration = Duration.ZERO,
    ) : DataStore<Preferences> {
        private val state = MutableStateFlow(emptyPreferences())
        override val data: Flow<Preferences> = state

        override suspend fun updateData(
            transform: suspend (t: Preferences) -> Preferences,
        ): Preferences {
            delay(writeDelay)
            return transform(state.value).also { state.value = it }
        }

        fun readString(key: String): String? = state.value[stringPreferencesKey(key)]
    }
}

private const val KEY = "theme"
private const val BOOLEAN_KEY = "onboarded"
private const val VALUE = "DARK"
private val WRITE_DELAY = 1.seconds
