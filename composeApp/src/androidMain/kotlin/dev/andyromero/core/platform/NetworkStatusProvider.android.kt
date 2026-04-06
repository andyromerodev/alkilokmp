package dev.andyromero.core.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

actual class NetworkStatusProvider actual constructor() {
    actual fun isOnline(): Boolean {
        val app = try {
            Class.forName("android.app.ActivityThread")
                .getMethod("currentApplication")
                .invoke(null) as? android.app.Application
        } catch (_: Throwable) {
            null
        } ?: return true

        return try {
            val manager =
                app.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                    ?: return false
            val network = manager.activeNetwork ?: return false
            val capabilities = manager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } catch (_: SecurityException) {
            false
        } catch (_: Throwable) {
            false
        }
    }
}
