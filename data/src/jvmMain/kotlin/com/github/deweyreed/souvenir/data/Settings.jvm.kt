package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.github.deweyreed.souvenir.base.api.Qualifiers
import me.sujanpoudel.utils.paths.appDataDirectory
import me.sujanpoudel.utils.paths.utils.div
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal actual val dataStoreModule: Module = module {
    single {
        newDataStore(get(named<Qualifiers.AppId>()))
    }
}

private fun newDataStore(appId: String): DataStore<Preferences> {
    return createDataStore(
        producePath = {
            val path = appDataDirectory(appId) / DATA_STORE_NAME
            path.toString()
        },
    )
}
