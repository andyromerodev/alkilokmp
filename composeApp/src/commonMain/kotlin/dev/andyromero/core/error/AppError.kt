package dev.andyromero.core.error

sealed class AppError(
    open val message: String,
    open val cause: Throwable? = null,
) {
    sealed class Network(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause) {
        data class NoConnection(
            override val message: String = "Sin conexion a internet",
        ) : Network(message)

        data class Timeout(
            override val message: String = "La solicitud tardo demasiado",
        ) : Network(message)

        data class ServerError(
            val code: Int,
            override val message: String = "Error del servidor",
        ) : Network(message)
    }

    sealed class Auth(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause) {
        data class Unauthorized(
            override val message: String = "No autorizado. Inicia sesion nuevamente.",
        ) : Auth(message)

        data class InvalidCredentials(
            override val message: String = "Correo o contrasena invalidos",
        ) : Auth(message)

        data class SessionExpired(
            override val message: String = "Sesion expirada. Inicia sesion nuevamente.",
        ) : Auth(message)

        data class EmailNotConfirmed(
            override val message: String = "Confirma tu correo electronico",
        ) : Auth(message)

        data class UserAlreadyExists(
            override val message: String = "El usuario ya existe",
        ) : Auth(message)
    }

    sealed class Data(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AppError(message, cause) {
        data class NotFound(
            override val message: String = "Recurso no encontrado",
        ) : Data(message)

        data class ValidationError(
            override val message: String,
        ) : Data(message)

        data class ParseError(
            override val message: String = "Error al procesar los datos",
        ) : Data(message)
    }

    data class Unknown(
        override val message: String = "Ocurrio un error inesperado",
        override val cause: Throwable? = null,
    ) : AppError(message, cause)
}
