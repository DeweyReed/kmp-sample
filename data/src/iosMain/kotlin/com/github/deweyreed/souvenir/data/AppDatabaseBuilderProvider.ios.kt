@file:OptIn(ExperimentalForeignApi::class)

package com.github.deweyreed.souvenir.data

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@ContributesTo(AppScope::class)
actual interface AppDatabaseBuilderProvider {
    @Provides
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = "${documentDirectory()}/$DATABASE_NAME"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
    }
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
