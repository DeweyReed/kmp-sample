package com.github.deweyreed.souvenir.data

import androidx.room.RoomDatabase
import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.deweyreed.souvenir.base.api.Qualifiers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val databaseModule = module {
    includes(databaseBuilderModule)
    singleOf(::getRoomDatabase)
    single(named<Qualifiers.DatabaseTransaction>()) {
        val database = get<AppDatabase>()
        val method: suspend (block: suspend () -> Unit) -> Unit = { block ->
            database.useWriterConnection {
                it.immediateTransaction {
                    block()
                }
            }
        }
        method
    }
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
