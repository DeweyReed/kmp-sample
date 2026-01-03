package com.github.deweyreed.souvenir.data

import com.github.deweyreed.souvenir.base.data.networkModule
import com.github.deweyreed.souvenir.feature.home.data.articleRepositoryModule
import org.koin.core.module.Module
import org.koin.dsl.module

val dataModule: Module = module {
    includes(
        networkModule,
        articleRepositoryModule,
    )
}
