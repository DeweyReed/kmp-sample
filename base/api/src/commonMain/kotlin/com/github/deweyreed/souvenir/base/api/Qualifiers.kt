package com.github.deweyreed.souvenir.base.api

import dev.zacsweers.metro.Qualifier

object Qualifiers {
    @Qualifier
    annotation class AppId

    object CoroutineScope {
        @Qualifier
        annotation class Application
    }

    object Dispatchers {
        @Qualifier
        annotation class Io

        @Qualifier
        annotation class Default
    }
}
