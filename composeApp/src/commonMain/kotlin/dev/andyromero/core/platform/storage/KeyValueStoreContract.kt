package dev.andyromero.core.platform.storage

internal interface KeyValueStoreContract {
    fun getString(key: String): String?
    fun putString(key: String, value: String)

    fun getLong(key: String): Long?
    fun putLong(key: String, value: Long)

    fun getStringSet(key: String): Set<String>
    fun putStringSet(key: String, value: Set<String>)

    fun remove(key: String)
}

internal expect class KeyValueStoreFactory() {
    fun create(name: String): KeyValueStoreContract
}
