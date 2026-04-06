package dev.andyromero.core.platform

expect class NetworkStatusProvider() {
    fun isOnline(): Boolean
}
