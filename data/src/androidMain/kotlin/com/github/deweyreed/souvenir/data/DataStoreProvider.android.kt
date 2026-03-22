package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
actual interface DataStoreProvider {
    @SingleIn(AppScope::class)
    @Provides
    fun newDataStore(): DataStore<Preferences> {
        return createDataStore(
            producePath = { applicationContext.filesDir.resolve(DATA_STORE_NAME).absolutePath },
        )
    }
}
