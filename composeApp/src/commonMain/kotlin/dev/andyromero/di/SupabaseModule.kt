package dev.andyromero.di

import dev.andyromero.core.platform.PlatformConfigProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.dsl.module

internal val supabaseModule = module {
    single<SupabaseClient> {
        val config = get<PlatformConfigProvider>()
        createSupabaseClient(
            supabaseUrl = config.supabaseUrl,
            supabaseKey = config.supabaseAnonKey,
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    single<Auth> { get<SupabaseClient>().auth }
    single<Postgrest> { get<SupabaseClient>().postgrest }
}
