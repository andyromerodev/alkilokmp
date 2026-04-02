package dev.andyromero

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform