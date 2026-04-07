package dev.andyromero.navigation

internal sealed interface Routes {
    data object Splash : Routes

    data object MainTabs : Routes
    data object HostTabs : Routes

    data object BeachList : Routes
    data object Favorites : Routes
    data object Settings : Routes

    data object PropertyList : Routes
    data class PropertyDetail(val propertyId: String) : Routes
    data object Profile : Routes

    data class Login(val returnPropertyId: String? = null) : Routes
    data class Register(val returnPropertyId: String? = null) : Routes

    data class CreateBooking(val propertyId: String) : Routes

    data object HostProperties : Routes
    data object HostBookings : Routes
    data object HostSettings : Routes
    data class HostPropertyDetail(val propertyId: String) : Routes
    data class HostEditProperty(val propertyId: String) : Routes
    data class HostCreateBooking(val propertyId: String) : Routes
    data class HostBookingDetail(val bookingId: String) : Routes

    data object AdminBookings : Routes
}
