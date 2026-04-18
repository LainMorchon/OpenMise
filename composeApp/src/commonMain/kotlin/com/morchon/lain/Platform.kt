package com.morchon.lain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform