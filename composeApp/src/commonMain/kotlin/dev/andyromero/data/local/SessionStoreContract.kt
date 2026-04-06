package dev.andyromero.data.local

import kotlinx.coroutines.flow.Flow

interface SessionStoreContract {
    val session: Flow<Session?>

    suspend fun saveSession(session: Session)
    suspend fun clearSession()
    suspend fun getSession(): Session?
}
