package com.github.deweyreed.souvenir.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.github.deweyreed.souvenir.feature.home.data.ArticleDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@ContributesTo(AppScope::class)
interface AppDatabaseProviders {
    @SingleIn(AppScope::class)
    @Provides
    fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
        return builder
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Provides
    fun getArticleDao(database: AppDatabase): ArticleDao = database.getArticleDao()
}

expect interface AppDatabaseBuilderProvider
