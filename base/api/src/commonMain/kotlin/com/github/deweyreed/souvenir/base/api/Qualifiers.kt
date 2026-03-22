package com.github.deweyreed.souvenir.base.api

import dev.zacsweers.metro.Qualifier

object Qualifiers {
    @Qualifier
    annotation class AppId

    object Dispatchers {
        @Qualifier
        annotation class Io

        @Qualifier
        annotation class Default
    }
}
