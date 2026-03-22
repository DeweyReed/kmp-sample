package com.github.deweyreed.souvenir.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.deweyreed.souvenir.base.api.Qualifiers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import me.sujanpoudel.utils.paths.appDataDirectory
import me.sujanpoudel.utils.paths.utils.div

@ContributesTo(AppScope::class)
actual interface AppDatabaseBuilderProvider {
    @Provides
    fun getDatabaseBuilder(
        @Qualifiers.AppId appId: String,
    ): RoomDatabase.Builder<AppDatabase> {
        val path = appDataDirectory(appId) / DATABASE_NAME
        return Room.databaseBuilder<AppDatabase>(
            name = path.toString(),
        )
    }
}
