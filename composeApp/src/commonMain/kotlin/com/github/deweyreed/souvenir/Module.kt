package com.github.deweyreed.souvenir

import com.github.deweyreed.souvenir.base.api.Qualifiers
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val appModule = module {
    single(named<Qualifiers.AppId>()) {
        "com.github.deweyreed.souvenir"
    }
}
