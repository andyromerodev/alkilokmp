package dev.andyromero.core.platform.storage

import android.content.Context
import android.content.SharedPreferences
import org.koin.core.context.GlobalContext
import androidx.core.content.edit

internal actual class KeyValueStoreFactory actual constructor() {
    actual fun create(name: String): KeyValueStoreContract {
        val context = GlobalContext.get().get<Context>()
        val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        return AndroidSharedPreferencesStoreImpl(sharedPreferences)
    }
}

private class AndroidSharedPreferencesStoreImpl(
    private val sharedPreferences: SharedPreferences,
) : KeyValueStoreContract {
    override fun getString(key: String): String? = sharedPreferences.getString(key, null)

    override fun putString(key: String, value: String) {
        sharedPreferences.edit { putString(key, value) }
    }

    override fun getLong(key: String): Long? {
        if (!sharedPreferences.contains(key)) return null
        return sharedPreferences.getLong(key, 0L)
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit { putLong(key, value) }
    }

    override fun getStringSet(key: String): Set<String> {
        return sharedPreferences.getStringSet(key, null)?.toSet().orEmpty()
    }

    override fun putStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit { putStringSet(key, value) }
    }

    override fun remove(key: String) {
        sharedPreferences.edit { remove(key) }
    }
}
