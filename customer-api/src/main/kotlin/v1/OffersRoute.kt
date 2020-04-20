package v1

import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.koin.ktor.ext.inject
import service.OfferService

//
//data class ChainResource(
//        val id: Int?,
//        val name: String,
//        val favorite: Boolean = false
//)
//
//private fun Offer.toResource() : ChainResource {
//    return ChainResource(this.id, this.name, this.favorite)
//}

fun Route.offers() {
    val offerService by inject<OfferService>()

    route("/offers") {
        get("/") {
            //TODO: map to resource models
            call.respond(offerService.getPromotedOffers())
        }
    }
}