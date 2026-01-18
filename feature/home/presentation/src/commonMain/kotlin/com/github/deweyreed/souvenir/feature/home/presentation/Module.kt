package com.github.deweyreed.souvenir.feature.home.presentation

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val homeModule: Module = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}
