package dev.andyromero.data.repository

import dev.andyromero.core.result.Result
import kotlin.test.Test
import kotlin.test.assertTrue

class ConfigErrorAuthRepositoryTest {
    @Test
    fun login_returns_error_when_configuration_is_missing() = kotlinx.coroutines.test.runTest {
        val repository = ConfigErrorAuthRepositoryImpl("Missing Supabase config")

        val result = repository.login("test@alkilo.dev", "1234")

        assertTrue(result is Result.Error)
    }
}

