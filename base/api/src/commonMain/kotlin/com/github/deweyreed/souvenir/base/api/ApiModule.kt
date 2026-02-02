package com.github.deweyreed.souvenir.base.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dispatchersModule: Module = module {
    single(named<Qualifiers.Dispatchers.Io>()) { Dispatchers.IO }
    single(named<Qualifiers.Dispatchers.Default>()) { Dispatchers.Default }
}
