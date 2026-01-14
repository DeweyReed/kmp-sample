package com.github.deweyreed.souvenir.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.deweyreed.souvenir.base.data.networkModule
import com.github.deweyreed.souvenir.feature.home.data.articleRepositoryModule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private val databaseModule = module {
    includes(databaseBuilderModule)
    singleOf(::getRoomDatabase)
    factory { get<AppDatabase>().getArticleDao() }
}

internal expect val databaseBuilderModule: Module

private fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

val dataModule: Module = module {
    includes(
        databaseModule,
        networkModule,
        articleRepositoryModule,
    )
}
