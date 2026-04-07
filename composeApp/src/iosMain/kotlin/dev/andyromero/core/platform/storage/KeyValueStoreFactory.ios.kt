package dev.andyromero.core.platform.storage

import platform.Foundation.NSUserDefaults

internal actual class KeyValueStoreFactory actual constructor() {
    actual fun create(name: String): KeyValueStoreContract {
        val defaults = NSUserDefaults(suiteName = name) ?: NSUserDefaults.standardUserDefaults
        return AppleUserDefaultsStoreImpl(defaults)
    }
}

private class AppleUserDefaultsStoreImpl(
    private val defaults: NSUserDefaults,
) : KeyValueStoreContract {
    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    override fun getLong(key: String): Long? {
        val value = defaults.objectForKey(key) ?: return null
        return (value as? Number)?.toLong()
    }

    override fun putLong(key: String, value: Long) {
        defaults.setObject(value, forKey = key)
    }

    override fun getStringSet(key: String): Set<String> {
        val value = defaults.stringForKey(key) ?: return emptySet()
        if (value.isBlank()) return emptySet()
        return value.split('|').filter { it.isNotBlank() }.toSet()
    }

    override fun putStringSet(key: String, value: Set<String>) {
        defaults.setObject(value.joinToString("|"), forKey = key)
    }

    override fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }
}
