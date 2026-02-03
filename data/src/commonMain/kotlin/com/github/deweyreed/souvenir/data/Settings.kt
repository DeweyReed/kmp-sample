package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.github.deweyreed.souvenir.base.api.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val settingsModule = module {
    includes(dataStoreModule)
    singleOf(::SettingsImpl).bind<Settings>()
}

internal expect val dataStoreModule: Module

private class SettingsImpl(private val dataStore: DataStore<Preferences>) : Settings {
    override fun getBooleanFlow(key: String): Flow<Boolean?> {
        val preferencesKey = booleanPreferencesKey(key)
        return dataStore.data.map { it[preferencesKey] }
    }

    override suspend fun setBoolean(key: String, value: Boolean?) {
        val preferencesKey = booleanPreferencesKey(key)
        dataStore.edit {
            if (value != null) {
                it[preferencesKey] = value
            } else {
                it.remove(preferencesKey)
            }
        }
    }

    override fun getStringFlow(key: String): Flow<String?> {
        val preferencesKey = stringPreferencesKey(key)
        return dataStore.data.map { it[preferencesKey] }
    }

    override suspend fun setString(key: String, value: String?) {
        val preferencesKey = stringPreferencesKey(key)
        dataStore.edit {
            if (value != null) {
                it[preferencesKey] = value
            } else {
                it.remove(preferencesKey)
            }
        }
    }
}

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
}

internal const val DATA_STORE_NAME = "app.preferences_pb"
