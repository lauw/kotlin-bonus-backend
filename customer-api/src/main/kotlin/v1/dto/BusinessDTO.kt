package v1.dto

import model.Business

data class BusinessDTO(
        val id: Int,
        val name: String,
        val active: Boolean,
        val bonuses: List<BonusDTO> = listOf(),
        val locations: List<LocationDTO> = listOf(),
        val offers: List<OfferDTO> = listOf()
) {
    companion object {
        fun fromDomain(business: Business): BusinessDTO =
                 BusinessDTO(
                         business.id,
                         business.name,
                         business.active,
                         business.bonuses.map { BonusDTO.fromDomain(it) },
                         business.locations.map { LocationDTO.fromDomain(it) },
                         business.offers.map { OfferDTO.fromDomain(it) }
                 )
    }
}

data class BusinessListDTO(
        val id: Int,
        val name: String,
        val favorite: Boolean = false
) {
    companion object {
        fun fromDomain(business: Business): BusinessListDTO =
                BusinessListDTO(
                        business.id,
                        business.name,
                        business.active
                )
    }
}