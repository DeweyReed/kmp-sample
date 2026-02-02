package com.github.deweyreed.souvenir.data

import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.deweyreed.souvenir.base.api.Qualifiers
import me.sujanpoudel.utils.paths.appDataDirectory
import me.sujanpoudel.utils.paths.utils.div
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val databaseBuilderModule: Module = module {
    factory {
        getDatabaseBuilder(get(named<Qualifiers.AppId>()))
    }
}

private fun getDatabaseBuilder(appId: String): RoomDatabase.Builder<AppDatabase> {
    val path = appDataDirectory(appId) / DATABASE_NAME
    return Room.databaseBuilder<AppDatabase>(
        name = path.toString(),
    )
}
