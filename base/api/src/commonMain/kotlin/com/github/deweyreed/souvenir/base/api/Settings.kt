package com.github.deweyreed.souvenir.base.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

interface Settings {
    fun getBooleanFlow(key: String): Flow<Boolean?>
    suspend fun getBoolean(key: String): Boolean? = getBooleanFlow(key).firstOrNull()
    suspend fun setBoolean(key: String, value: Boolean?)
}
