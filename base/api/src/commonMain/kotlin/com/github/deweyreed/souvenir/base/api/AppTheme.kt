package com.github.deweyreed.souvenir.base.api

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class AppTheme {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): AppTheme {
            return entries.find { it.name == value } ?: SYSTEM
        }
    }
}

private const val KEY_THEME: String = "theme"
fun Settings.getAppThemeFlow(): Flow<AppTheme> {
    return getStringFlow(KEY_THEME).map { AppTheme.fromString(it) }
}

suspend fun Settings.setAppTheme(value: AppTheme) {
    setString(KEY_THEME, value.name)
}
