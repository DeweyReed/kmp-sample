package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val articleRepositoryModule = module {
    factory {
        ArticleRepositoryImpl(
            get(named<Qualifiers.Dispatchers.Io>()),
            get(),
            get(),
            get(),
        )
    }.bind<ArticleRepository>()
}

private class ArticleRepositoryImpl(
    private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
    private val dao: ArticleDao,
    private val httpClient: HttpClient,
) : ArticleRepository {
    override fun getItemsPagination(coroutineScope: CoroutineScope): Pagination<ArticleEntity> {
        return Pagination(
            flow = dao.getItemsFlow()
                .onStart {
                    if (settings.getBoolean(KEY_HAS_MORE_ITEMS) != false &&
                        dao.getItemCount() == 0
                    ) {
                        loadMore(coroutineScope)
                    }
                }
                .map { it.map(ArticleData::toEntity) }
                .flowOn(ioDispatcher),
            loadMore = { loadMore(coroutineScope) },
        )
    }

    private fun loadMore(coroutineScope: CoroutineScope) {
        if (itemsMutex.isLocked) return
        coroutineScope.launch(ioDispatcher) {
            if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@launch
            itemsMutex.withLock {
                if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@withLock
                val url = settings.getString(KEY_ITEMS_NEXT_PAGE) ?: "articles"
                val data = httpClient.get(url)
                    .body<ArticleRemoteData>()
                val nextPage = data.next
                settings.setString(KEY_ITEMS_NEXT_PAGE, nextPage)
                val items = data
                    .results
                    .map(ArticleRemoteData.Result::toData)
                dao.insertItemsWithoutDuplicates(items)
                val hasMore = nextPage != null
                settings.setBoolean(KEY_HAS_MORE_ITEMS, hasMore)
            }
        }
    }

    override fun getItemFlow(id: Long): Flow<ArticleEntity?> {
        return dao.getItemFlow(id).map { it?.toEntity() }
    }

    override suspend fun clearItems(): Unit = withContext(ioDispatcher) {
        dao.clearItems()
        settings.setBoolean(KEY_HAS_MORE_ITEMS, true)
        settings.setString(KEY_ITEMS_NEXT_PAGE, null)
    }
}

private val itemsMutex = Mutex()

private const val KEY_HAS_MORE_ITEMS = "has_more_items"
private const val KEY_ITEMS_NEXT_PAGE = "items_next_page"
