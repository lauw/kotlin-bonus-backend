package v1.dto

import model.Offer
import java.time.LocalDateTime

data class OfferDTO(
    val id: Int,
    val name: String,
    val description: String,
    val imgUri: String,
    val scanRequired: Boolean,
    val endDate: LocalDateTime
) {
    companion object {
        fun fromDomain(offer: Offer): OfferDTO =
                OfferDTO(offer.id, offer.name, offer.description, offer.imgUri, offer.scanRequired, offer.endDate)
    }
}
