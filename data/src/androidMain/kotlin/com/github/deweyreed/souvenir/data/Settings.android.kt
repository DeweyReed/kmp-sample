package com.github.deweyreed.souvenir.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual val dataStoreModule: Module = module {
    singleOf(::newDataStore)
}

private fun newDataStore(): DataStore<Preferences> {
    return createDataStore(
        producePath = { applicationContext.filesDir.resolve(DATA_STORE_NAME).absolutePath },
    )
}
