package dev.andyromero.core.platform.storage

internal actual class KeyValueStoreFactory actual constructor() {
    actual fun create(name: String): KeyValueStoreContract = InMemoryKeyValueStoreImpl(name)
}

private class InMemoryKeyValueStoreImpl(
    private val namespace: String,
) : KeyValueStoreContract {
    private val storage = mutableMapOf<String, String>()

    private fun namespaced(key: String): String = "$namespace:$key"

    override fun getString(key: String): String? = storage[namespaced(key)]

    override fun putString(key: String, value: String) {
        storage[namespaced(key)] = value
    }

    override fun getLong(key: String): Long? = storage[namespaced(key)]?.toLongOrNull()

    override fun putLong(key: String, value: Long) {
        storage[namespaced(key)] = value.toString()
    }

    override fun getStringSet(key: String): Set<String> {
        val value = storage[namespaced(key)] ?: return emptySet()
        if (value.isBlank()) return emptySet()
        return value.split('|').filter { it.isNotBlank() }.toSet()
    }

    override fun putStringSet(key: String, value: Set<String>) {
        storage[namespaced(key)] = value.joinToString("|")
    }

    override fun remove(key: String) {
        storage.remove(namespaced(key))
    }
}
