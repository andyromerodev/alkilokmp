package dev.andyromero.navigation

internal sealed interface Routes {
    data object Splash : Routes

    data object MainTabs : Routes

    data object BeachList : Routes
    data object Favorites : Routes
    data object Settings : Routes

    data object PropertyList : Routes
    data class PropertyDetail(val propertyId: String) : Routes
    data object Profile : Routes

    data class Login(val returnPropertyId: String? = null) : Routes
    data class Register(val returnPropertyId: String? = null) : Routes

    data class CreateBooking(val propertyId: String, val returnToPropertyDetail: Boolean = false) : Routes

}
