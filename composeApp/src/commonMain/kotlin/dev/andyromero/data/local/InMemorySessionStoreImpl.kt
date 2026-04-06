package dev.andyromero.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InMemorySessionStoreImpl : SessionStoreContract {
    private val holder = MutableStateFlow<Session?>(null)

    override val session: Flow<Session?> = holder.asStateFlow()

    override suspend fun saveSession(session: Session) {
        holder.value = session
    }

    override suspend fun clearSession() {
        holder.value = null
    }

    override suspend fun getSession(): Session? = holder.value
}
