package dev.andyromero.core.platform

actual class PlatformConfigProvider actual constructor() {
    actual val supabaseUrl: String = GeneratedSupabaseConfig.SUPABASE_URL
    actual val supabaseAnonKey: String = GeneratedSupabaseConfig.SUPABASE_ANON_KEY
}
