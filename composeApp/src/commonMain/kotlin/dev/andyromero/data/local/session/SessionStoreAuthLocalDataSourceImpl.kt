package dev.andyromero.data.local.session

import dev.andyromero.core.logging.Logger
import kotlinx.coroutines.flow.Flow

internal class SessionStoreAuthLocalDataSourceImpl(
    private val sessionStore: SessionStoreContract,
    private val logger: Logger,
) : AuthLocalDataSourceContract {
    private companion object {
        const val TAG = "SessionAuthLocalDS"
    }

    override val session: Flow<Session?> = sessionStore.session

    override suspend fun saveSession(session: Session) {
        logger.d(TAG, "saveSession userId=${session.userId}")
        sessionStore.saveSession(session)
    }

    override suspend fun clearSession() {
        logger.d(TAG, "clearSession")
        sessionStore.clearSession()
    }

    override suspend fun getSession(): Session? {
        val session = sessionStore.getSession()
        logger.d(TAG, "getSession found=${session != null}")
        return session
    }
}
