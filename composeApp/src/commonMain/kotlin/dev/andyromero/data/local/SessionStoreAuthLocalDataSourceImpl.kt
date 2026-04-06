package dev.andyromero.data.local

import kotlinx.coroutines.flow.Flow

internal class SessionStoreAuthLocalDataSourceImpl(
    private val sessionStore: SessionStoreContract,
) : AuthLocalDataSourceContract {
    override val session: Flow<Session?> = sessionStore.session

    override suspend fun saveSession(session: Session) {
        sessionStore.saveSession(session)
    }

    override suspend fun clearSession() {
        sessionStore.clearSession()
    }

    override suspend fun getSession(): Session? = sessionStore.getSession()
}
