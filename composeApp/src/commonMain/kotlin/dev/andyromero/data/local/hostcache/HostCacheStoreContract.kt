package dev.andyromero.data.local.hostcache

import dev.andyromero.data.local.hostcache.model.BookingRequestCache
import dev.andyromero.data.local.hostcache.model.PropertyCache

internal interface HostCacheStoreContract {
    suspend fun getHostProperties(hostId: String): List<PropertyCache>
    suspend fun saveHostProperties(hostId: String, properties: List<PropertyCache>)

    suspend fun getHostBookings(hostId: String): List<BookingRequestCache>
    suspend fun saveHostBookings(hostId: String, bookings: List<BookingRequestCache>)

    suspend fun clearAll()
}
