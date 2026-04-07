package dev.andyromero.presentation.auth.shared

internal object AuthValidators {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    fun emailError(value: String): String? {
        return if (value.isBlank() || emailRegex.matches(value)) null else "Email inválido"
    }

    fun passwordError(value: String): String? {
        return if (value.isBlank() || value.length >= 6) null else "Mínimo 6 caracteres"
    }
}
