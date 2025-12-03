package com.github.deweyreed.souvenir

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
