package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.base.api.Settings
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class ArticleRepositoryImpl(
    @param:Qualifiers.Dispatchers.Io private val ioDispatcher: CoroutineDispatcher,
    private val settings: Settings,
    private val dao: ArticleDao,
    private val httpClient: HttpClient,
) : ArticleRepository {
    private val itemsMutex = Mutex()

    override fun getItemsPagination(): Pagination<ArticleEntity> {
        return Pagination(
            flow = dao.getItemsFlow()
                .onStart {
                    if (settings.getBoolean(KEY_HAS_MORE_ITEMS) != false &&
                        dao.getItemCount() == 0
                    ) {
                        loadMore()
                    }
                }
                .map { it.map(ArticleData::toEntity) }
                .flowOn(ioDispatcher),
            loadMore = ::loadMore,
        )
    }

    private suspend fun loadMore(): Unit = withContext(ioDispatcher) {
        if (itemsMutex.isLocked) return@withContext
        if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@withContext
        itemsMutex.withLock {
            if (settings.getBoolean(KEY_HAS_MORE_ITEMS) == false) return@withLock
            try {
                val url = settings.getString(KEY_ITEMS_NEXT_PAGE) ?: "articles"
                val data =
                    httpClient.get(url)
                        .body<ArticleRemoteData>()
                val nextPage = data.next
                settings.setString(KEY_ITEMS_NEXT_PAGE, nextPage)
                val items = data
                    .results
                    .map(ArticleRemoteData.Result::toData)
                val hasMore = nextPage != null
                dao.insertItemsWithoutDuplicates(items)
                settings.setBoolean(KEY_HAS_MORE_ITEMS, hasMore)
            } catch (_: Exception) {
                ensureActive()
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

private const val KEY_HAS_MORE_ITEMS = "article_has_more_items"
private const val KEY_ITEMS_NEXT_PAGE = "article_items_next_page"
