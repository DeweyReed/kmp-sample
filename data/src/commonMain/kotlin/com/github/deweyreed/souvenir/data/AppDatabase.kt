package com.github.deweyreed.souvenir.data

import androidx.room3.AutoMigration
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.github.deweyreed.souvenir.feature.home.data.ArticleDao
import com.github.deweyreed.souvenir.feature.home.data.ArticleData
import com.github.deweyreed.souvenir.feature.home.data.ArticlePagingStateData

@Database(
    entities = [
        ArticleData::class,
        ArticlePagingStateData::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getArticleDao(): ArticleDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

internal const val DATABASE_NAME = "app.db"
