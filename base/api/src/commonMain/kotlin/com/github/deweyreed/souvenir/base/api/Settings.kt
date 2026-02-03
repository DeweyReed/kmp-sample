package com.github.deweyreed.souvenir.base.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface Settings {
    fun getBooleanFlow(key: String): Flow<Boolean?>
    suspend fun getBoolean(key: String): Boolean? = getBooleanFlow(key).firstOrNull()
    suspend fun setBoolean(key: String, value: Boolean?)

    fun getStringFlow(key: String): Flow<String?>
    suspend fun getString(key: String): String? = getStringFlow(key).firstOrNull()
    suspend fun setString(key: String, value: String?)
}
