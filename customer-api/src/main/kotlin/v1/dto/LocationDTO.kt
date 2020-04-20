package v1.dto

import model.Location

data class LocationDTO(
        val id: Int,
        val name: String,
        val address: String,
        val city: String,
        val imgUri: String,
        val latitude: Int,
        val longitude: Int
) {
    companion object {
        fun fromDomain(location: Location): LocationDTO =
                 LocationDTO(location.id, location.name, location.address, location.city, location.imgUri, location.latitude, location.longitude)
    }
}
