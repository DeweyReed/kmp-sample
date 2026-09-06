package com.github.deweyreed.souvenir.base.api

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob

@ContributesTo(AppScope::class)
interface BaseProviders {

    @SingleIn(AppScope::class)
    @Provides
    @Qualifiers.CoroutineScope.Application
    fun getApplicationScope(
        @Qualifiers.Dispatchers.Default dispatcher: CoroutineDispatcher,
    ): CoroutineScope {
        return CoroutineScope(
            SupervisorJob() + dispatcher + CoroutineName("ApplicationScope")
        )
    }

    @SingleIn(AppScope::class)
    @Provides
    @Qualifiers.Dispatchers.Io
    fun getIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @SingleIn(AppScope::class)
    @Provides
    @Qualifiers.Dispatchers.Default
    fun getDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}
