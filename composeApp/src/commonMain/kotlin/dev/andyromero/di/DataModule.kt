package dev.andyromero.di

import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.platform.NetworkStatusProvider
import dev.andyromero.core.platform.PlatformConfigProvider
import dev.andyromero.core.platform.storage.KeyValueStoreFactory
import dev.andyromero.data.local.favorites.FavoritesStoreContract
import dev.andyromero.data.local.favorites.FavoritesStoreImpl
import dev.andyromero.data.local.session.AuthLocalDataSourceContract
import dev.andyromero.data.local.session.SessionStoreAuthLocalDataSourceImpl
import dev.andyromero.data.local.session.SessionStoreContract
import dev.andyromero.data.local.session.SessionStoreImpl
import dev.andyromero.data.local.settings.ThemeSettingsStoreContract
import dev.andyromero.data.local.settings.ThemeSettingsStoreImpl
import dev.andyromero.data.remote.AuthRemoteDataSourceContract
import dev.andyromero.data.remote.SupabaseAuthRemoteDataSourceImpl
import dev.andyromero.data.remote.property.PropertyRemoteDataSourceContract
import dev.andyromero.data.remote.property.SupabasePropertyRemoteDataSourceImpl
import dev.andyromero.data.repository.ConfigErrorAuthRepositoryImpl
import dev.andyromero.data.repository.FavoritesRepositoryImpl
import dev.andyromero.data.repository.SupabaseAuthRepositoryImpl
import dev.andyromero.data.repository.SupabasePropertyRepositoryImpl
import dev.andyromero.data.repository.ThemeRepositoryImpl
import dev.andyromero.domain.repository.AuthRepositoryContract
import dev.andyromero.domain.repository.FavoritesRepositoryContract
import dev.andyromero.domain.repository.PropertyRepositoryContract
import dev.andyromero.domain.repository.ThemeRepositoryContract
import org.koin.dsl.module

internal val dataModule = module {
    single { KeyValueStoreFactory() }

    single<SessionStoreContract> { SessionStoreImpl(keyValueStoreFactory = get()) }
    single<ThemeSettingsStoreContract> { ThemeSettingsStoreImpl(keyValueStoreFactory = get()) }
    single<FavoritesStoreContract> { FavoritesStoreImpl(keyValueStoreFactory = get()) }

    single<AuthLocalDataSourceContract> {
        SessionStoreAuthLocalDataSourceImpl(
            sessionStore = get(),
            logger = get(),
        )
    }

    single<AuthRemoteDataSourceContract> {
        SupabaseAuthRemoteDataSourceImpl(
            auth = get(),
            postgrest = get(),
            logger = get(),
        )
    }

    single<PropertyRemoteDataSourceContract> {
        SupabasePropertyRemoteDataSourceImpl(
            postgrest = get(),
            logger = get(),
        )
    }

    single<AuthRepositoryContract> {
        val config = get<PlatformConfigProvider>()
        val logger = get<Logger>()

        if (config.supabaseUrl.isBlank() || config.supabaseAnonKey.isBlank()) {
            logger.w(
                tag = "KoinAuthRepository",
                message = "SUPABASE_URL o SUPABASE_ANON_KEY vacios.",
            )
            ConfigErrorAuthRepositoryImpl(
                message = "Configura SUPABASE_URL y SUPABASE_ANON_KEY para autenticar.",
            )
        } else {
            runCatching {
                SupabaseAuthRepositoryImpl(
                    remoteDataSource = get(),
                    localDataSource = get(),
                    networkStatusProvider = get<NetworkStatusProvider>(),
                    logger = logger,
                    dispatcherProvider = get<DispatcherProvider>(),
                )
            }.getOrElse { throwable ->
                logger.e(
                    tag = "KoinAuthRepository",
                    message = "Fallo inicializando Supabase.",
                    throwable = throwable,
                )
                ConfigErrorAuthRepositoryImpl(
                    message = "No se pudo inicializar Supabase. Revisa dependencias y configuracion.",
                )
            }
        }
    }

    single<ThemeRepositoryContract> {
        ThemeRepositoryImpl(store = get())
    }

    single<FavoritesRepositoryContract> {
        FavoritesRepositoryImpl(store = get())
    }

    single<PropertyRepositoryContract> {
        SupabasePropertyRepositoryImpl(
            remoteDataSource = get(),
            logger = get(),
            dispatcherProvider = get(),
        )
    }
}
