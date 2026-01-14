package com.github.deweyreed.souvenir.data

import android.content.Context
import androidx.startup.Initializer

internal lateinit var applicationContext: Context

@Suppress("unused")
internal class ApplicationContextInitializer : Initializer<Unit> {
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
    override fun create(context: Context) {
        applicationContext = context.applicationContext
    }
}
