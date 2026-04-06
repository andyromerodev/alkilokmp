package dev.andyromero.core.platform

expect class PlatformConfigProvider() {
    val supabaseUrl: String
    val supabaseAnonKey: String
}
