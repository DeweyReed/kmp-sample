package com.github.deweyreed.souvenir.base.api

import kotlinx.coroutines.flow.Flow

class Pagination<T>(
    val flow: Flow<List<T>>,
    val loadMore: () -> Unit,
)
