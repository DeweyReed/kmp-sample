package com.github.deweyreed.souvenir.feature.home.data

import com.github.deweyreed.souvenir.base.api.Pagination
import com.github.deweyreed.souvenir.base.api.Qualifiers
import com.github.deweyreed.souvenir.feature.home.api.ArticleEntity
import com.github.deweyreed.souvenir.feature.home.api.ArticleRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@ContributesBinding(AppScope::class)
@Inject
class ArticleRepositoryImpl(
    @param:Qualifiers.Dispatchers.Io private val ioDispatcher: CoroutineDispatcher,
    private val dao: ArticleDao,
    private val httpClient: HttpClient,
) : ArticleRepository {
    private val itemsMutex = Mutex()
    private val loadMoreMutex = Mutex()

    override fun getItemsPagination(): Pagination<ArticleEntity> {
        return Pagination(
            flow = dao.getItemsFlow()
                .map { it.map(ArticleData::toEntity) }
                .flowOn(ioDispatcher),
            loadMore = ::loadMore,
        )
    }

    private suspend fun loadMore() {
        if (!loadMoreMutex.tryLock()) return
        try {
            itemsMutex.withLock {
                withContext(ioDispatcher) {
                    val url = dao.getNextPage() ?: return@withContext
                    val data = requestPage(url) ?: return@withContext
                    dao.appendPage(items = data.toDataList(), nextPage = data.next)
                }
            }
        } finally {
            loadMoreMutex.unlock()
        }
    }

    override fun getItemFlow(id: Long): Flow<ArticleEntity?> {
        return dao.getItemFlow(id).map { it?.toEntity() }
    }

    override suspend fun refreshItems() {
        itemsMutex.withLock {
            withContext(ioDispatcher) {
                val data = requestPage(FIRST_ITEMS_PAGE) ?: return@withContext
                dao.replacePage(items = data.toDataList(), nextPage = data.next)
            }
        }
    }

    private suspend fun requestPage(url: String): ArticleRemoteData? {
        return try {
            httpClient.get(url).body<ArticleRemoteData>()
        } catch (_: Exception) {
            currentCoroutineContext().ensureActive()
            null
        }
    }
}

private const val FIRST_ITEMS_PAGE = "articles"
