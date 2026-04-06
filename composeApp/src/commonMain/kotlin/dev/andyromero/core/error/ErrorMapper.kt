package dev.andyromero.core.error

object ErrorMapper {
    fun mapException(exception: Throwable): AppError {
        val message = exception.message?.lowercase().orEmpty()
        return when {
            "timeout" in message -> AppError.Network.Timeout()
            "unauthorized" in message || "401" in message -> AppError.Auth.Unauthorized()
            "invalid login" in message || "invalid" in message && "credential" in message -> AppError.Auth.InvalidCredentials()
            "already" in message && "exist" in message -> AppError.Auth.UserAlreadyExists()
            "not found" in message -> AppError.Data.NotFound()
            else -> AppError.Unknown(cause = exception)
        }
    }
}
