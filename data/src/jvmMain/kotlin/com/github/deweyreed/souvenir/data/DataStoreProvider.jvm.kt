package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.deweyreed.souvenir.base.api.Qualifiers
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import me.sujanpoudel.utils.paths.appDataDirectory
import me.sujanpoudel.utils.paths.utils.div

@ContributesTo(AppScope::class)
actual interface DataStoreProvider {
    @SingleIn(AppScope::class)
    @Provides
    fun newDataStore(@Qualifiers.AppId appId: String): DataStore<Preferences> {
        return createDataStore(
            producePath = {
                val path = appDataDirectory(appId) / DATA_STORE_NAME
                path.toString()
            },
        )
    }
}
