package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeSettings : Settings {
    private val booleans =
        MutableStateFlow<Map<String, Boolean?>>(emptyMap())
    private val strings =
        MutableStateFlow<Map<String, String?>>(emptyMap())

    override fun getBooleanFlow(key: String): Flow<Boolean?> = booleans.map { it[key] }
    override suspend fun setBoolean(key: String, value: Boolean?) {
        booleans.update { it + (key to value) }
    }

    override fun getStringFlow(key: String): Flow<String?> = strings.map { it[key] }
    override suspend fun setString(key: String, value: String?) {
        strings.update { it + (key to value) }
    }
}
