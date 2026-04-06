package dev.andyromero.di

import dev.andyromero.core.dispatcher.DefaultDispatcherProvider
import dev.andyromero.core.dispatcher.DispatcherProvider
import dev.andyromero.core.logging.Logger
import dev.andyromero.core.platform.NetworkStatusProvider
import dev.andyromero.core.platform.PlatformConfigProvider
import dev.andyromero.core.platform.PlatformLogger
import dev.andyromero.data.local.AuthLocalDataSourceContract
import dev.andyromero.data.local.InMemorySessionStoreImpl
import dev.andyromero.data.local.InMemoryThemeSettingsStoreImpl
import dev.andyromero.data.local.SessionStoreAuthLocalDataSourceImpl
import dev.andyromero.data.local.SessionStoreContract
import dev.andyromero.data.local.ThemeSettingsStoreContract
import dev.andyromero.data.remote.AuthRemoteDataSourceContract
import dev.andyromero.data.remote.SupabaseAuthRemoteDataSourceImpl
import dev.andyromero.data.repository.ConfigErrorAuthRepositoryImpl
import dev.andyromero.data.repository.SupabaseAuthRepositoryImpl
import dev.andyromero.data.repository.ThemeRepositoryImpl
import dev.andyromero.domain.repository.AuthRepositoryContract
import dev.andyromero.domain.repository.ThemeRepositoryContract
import dev.andyromero.domain.usecase.auth.GetCurrentUserUseCase
import dev.andyromero.domain.usecase.auth.LoginUseCase
import dev.andyromero.domain.usecase.auth.LogoutUseCase
import dev.andyromero.domain.usecase.auth.ObserveAuthStateUseCase
import dev.andyromero.domain.usecase.auth.ObserveCurrentProfileUseCase
import dev.andyromero.domain.usecase.auth.RegisterUseCase
import dev.andyromero.domain.usecase.auth.RestoreSessionUseCase
import dev.andyromero.domain.usecase.settings.ObserveThemeModeUseCase
import dev.andyromero.domain.usecase.settings.UpdateThemeModeUseCase
import dev.andyromero.presentation.auth.AuthViewModel
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal val sharedAppModule = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    single { NetworkStatusProvider() }
    single { PlatformConfigProvider() }
    single<Logger> { PlatformLogger() }

    single<SessionStoreContract> { InMemorySessionStoreImpl() }
    single<ThemeSettingsStoreContract> { InMemoryThemeSettingsStoreImpl() }
    single<AuthLocalDataSourceContract> { SessionStoreAuthLocalDataSourceImpl(get()) }

    single<AuthRemoteDataSourceContract> {
        val config = get<PlatformConfigProvider>()
        val client = createSupabaseClient(
            supabaseUrl = config.supabaseUrl,
            supabaseKey = config.supabaseAnonKey,
        ) {
            install(Auth)
            install(Postgrest)
        }
        SupabaseAuthRemoteDataSourceImpl(
            auth = client.auth,
            postgrest = client.postgrest,
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
                    networkStatusProvider = get(),
                    logger = logger,
                    dispatcherProvider = get(),
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

    single<ThemeRepositoryContract> { ThemeRepositoryImpl(get()) }

    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { ObserveAuthStateUseCase(get()) }
    factory { ObserveCurrentProfileUseCase(get()) }
    factory { RestoreSessionUseCase(get()) }
    factory { ObserveThemeModeUseCase(get()) }
    factory { UpdateThemeModeUseCase(get()) }
    factory { AuthViewModel(get(), get(), get()) }
}

internal expect fun platformModule(): Module

internal fun initKoinIfNeeded(appDeclaration: KoinAppDeclaration = {}) {
    if (KoinPlatform.getKoinOrNull() != null) return
    startKoin {
        appDeclaration()
        modules(sharedAppModule, platformModule())
    }
}
