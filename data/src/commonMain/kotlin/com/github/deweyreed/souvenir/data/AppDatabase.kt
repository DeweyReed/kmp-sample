package com.github.deweyreed.souvenir.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.github.deweyreed.souvenir.feature.home.data.ArticleDao
import com.github.deweyreed.souvenir.feature.home.data.ArticleData

@Database(
    entities = [
        ArticleData::class,
    ],
    version = 1,
)
@ConstructedBy(AppDatabaseConstructor::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal const val DATABASE_NAME = "app.db"
