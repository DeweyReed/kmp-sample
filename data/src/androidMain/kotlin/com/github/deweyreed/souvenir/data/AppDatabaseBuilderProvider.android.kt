package com.github.deweyreed.souvenir.data

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides

@ContributesTo(AppScope::class)
actual interface AppDatabaseBuilderProvider {
    @Provides
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = applicationContext.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = applicationContext,
            name = dbFile.absolutePath,
        )
    }
}
