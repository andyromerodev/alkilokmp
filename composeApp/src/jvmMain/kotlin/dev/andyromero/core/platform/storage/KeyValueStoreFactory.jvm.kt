package dev.andyromero.core.platform.storage

import java.util.prefs.Preferences

internal actual class KeyValueStoreFactory actual constructor() {
    actual fun create(name: String): KeyValueStoreContract {
        val preferences = Preferences.userRoot().node("dev.andyromero.alkilokmp/$name")
        return JvmPreferencesStoreImpl(preferences)
    }
}

private class JvmPreferencesStoreImpl(
    private val preferences: Preferences,
) : KeyValueStoreContract {
    override fun getString(key: String): String? = preferences.get(key, null)

    override fun putString(key: String, value: String) {
        preferences.put(key, value)
    }

    override fun getLong(key: String): Long? {
        val raw = preferences.get(key, null) ?: return null
        return raw.toLongOrNull()
    }

    override fun putLong(key: String, value: Long) {
        preferences.putLong(key, value)
    }

    override fun getStringSet(key: String): Set<String> {
        val raw = preferences.get(key, null) ?: return emptySet()
        if (raw.isBlank()) return emptySet()
        return raw.split('|').filter { it.isNotBlank() }.toSet()
    }

    override fun putStringSet(key: String, value: Set<String>) {
        preferences.put(key, value.joinToString("|"))
    }

    override fun remove(key: String) {
        preferences.remove(key)
    }
}
