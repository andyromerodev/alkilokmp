package dev.andyromero.data.local.hostcache

import dev.andyromero.core.platform.storage.KeyValueStoreFactory
import dev.andyromero.data.local.hostcache.model.BookingRequestCache
import dev.andyromero.data.local.hostcache.model.PropertyCache
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class HostCacheStoreImpl(
    keyValueStoreFactory: KeyValueStoreFactory,
) : HostCacheStoreContract {
    private val store = keyValueStoreFactory.create("host_cache_store")
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun getHostProperties(hostId: String): List<PropertyCache> {
        val data = store.getString(hostPropertiesKey(hostId)) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(PropertyCache.serializer()), data)
        }.getOrDefault(emptyList())
    }

    override suspend fun saveHostProperties(hostId: String, properties: List<PropertyCache>) {
        val serialized = json.encodeToString(ListSerializer(PropertyCache.serializer()), properties)
        store.putString(hostPropertiesKey(hostId), serialized)
        registerHostId(KEY_HOST_IDS_PROPERTIES, hostId)
    }

    override suspend fun getHostBookings(hostId: String): List<BookingRequestCache> {
        val data = store.getString(hostBookingsKey(hostId)) ?: return emptyList()
        return runCatching {
            json.decodeFromString(ListSerializer(BookingRequestCache.serializer()), data)
        }.getOrDefault(emptyList())
    }

    override suspend fun saveHostBookings(hostId: String, bookings: List<BookingRequestCache>) {
        val serialized = json.encodeToString(ListSerializer(BookingRequestCache.serializer()), bookings)
        store.putString(hostBookingsKey(hostId), serialized)
        registerHostId(KEY_HOST_IDS_BOOKINGS, hostId)
    }

    override suspend fun clearAll() {
        val hostIdsForProperties = store.getStringSet(KEY_HOST_IDS_PROPERTIES)
        hostIdsForProperties.forEach { hostId ->
            store.remove(hostPropertiesKey(hostId))
        }

        val hostIdsForBookings = store.getStringSet(KEY_HOST_IDS_BOOKINGS)
        hostIdsForBookings.forEach { hostId ->
            store.remove(hostBookingsKey(hostId))
        }

        store.remove(KEY_HOST_IDS_PROPERTIES)
        store.remove(KEY_HOST_IDS_BOOKINGS)
    }

    private fun registerHostId(indexKey: String, hostId: String) {
        val current = store.getStringSet(indexKey).toMutableSet()
        current.add(hostId)
        store.putStringSet(indexKey, current)
    }

    private fun hostPropertiesKey(hostId: String): String = "host_properties_$hostId"

    private fun hostBookingsKey(hostId: String): String = "host_bookings_$hostId"

    private companion object {
        const val KEY_HOST_IDS_PROPERTIES = "host_ids_properties"
        const val KEY_HOST_IDS_BOOKINGS = "host_ids_bookings"
    }
}
