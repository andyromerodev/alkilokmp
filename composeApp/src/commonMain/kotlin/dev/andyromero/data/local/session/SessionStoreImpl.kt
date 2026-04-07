package dev.andyromero.data.local.session

import dev.andyromero.core.platform.storage.KeyValueStoreFactory
import dev.andyromero.domain.model.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class SessionStoreImpl(
    keyValueStoreFactory: KeyValueStoreFactory,
) : SessionStoreContract {
    private val store = keyValueStoreFactory.create("session_store")

    private val _session = MutableStateFlow(readSession())
    override val session: Flow<Session?> = _session.asStateFlow()

    override suspend fun saveSession(session: Session) {
        store.putString(KEY_ACCESS_TOKEN, session.accessToken)
        store.putString(KEY_REFRESH_TOKEN, session.refreshToken)
        store.putLong(KEY_EXPIRES_AT, session.expiresAt)
        store.putString(KEY_USER_ID, session.userId)
        store.putString(KEY_EMAIL, session.email)
        store.putString(KEY_ROLE, session.role.name)
        _session.value = session
    }

    override suspend fun clearSession() {
        store.remove(KEY_ACCESS_TOKEN)
        store.remove(KEY_REFRESH_TOKEN)
        store.remove(KEY_EXPIRES_AT)
        store.remove(KEY_USER_ID)
        store.remove(KEY_EMAIL)
        store.remove(KEY_ROLE)
        _session.value = null
    }

    override suspend fun getSession(): Session? = _session.value

    private fun readSession(): Session? {
        val accessToken = store.getString(KEY_ACCESS_TOKEN) ?: return null
        val refreshToken = store.getString(KEY_REFRESH_TOKEN) ?: return null
        val expiresAt = store.getLong(KEY_EXPIRES_AT) ?: return null
        val userId = store.getString(KEY_USER_ID) ?: return null
        val email = store.getString(KEY_EMAIL) ?: return null
        val roleString = store.getString(KEY_ROLE) ?: return null

        return Session(
            accessToken = accessToken,
            refreshToken = refreshToken,
            expiresAt = expiresAt,
            userId = userId,
            email = email,
            role = runCatching { UserRole.valueOf(roleString) }.getOrDefault(UserRole.CLIENT),
        )
    }

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_USER_ID = "user_id"
        const val KEY_EMAIL = "email"
        const val KEY_ROLE = "role"
    }
}
