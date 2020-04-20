package v1

import auth.CustomerPrincipal
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import org.koin.ktor.ext.inject
import service.BusinessService
import v1.dto.BusinessDTO
import v1.dto.BusinessListDTO

/*
JWT user 1
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJib251cy1jdXN0b21lciIsImlzcyI6Imh0dHBzOi8vYm9udXMtYXBwLmNvbSIsImlkIjoxLCJleHAiOjE1OTY0NDc3MTAsImVtYWlsIjoibGF1d2FsaWFzQGdtYWlsLmNvbSJ9.YE2apBIBJ80kyiA7-E0iAhJ9hk8khD_sWrKbywv9nPY

JWT user 2
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJib251cy1jdXN0b21lciIsImlzcyI6Imh0dHBzOi8vYm9udXMtYXBwLmNvbSIsImlkIjoyLCJleHAiOjE1OTY0NDc3MTAsImVtYWlsIjoibGF1d2FsaWFzQGdtYWlsLmNvbSJ9.Mmz_iMHh0FXVM-MG-C_gME6_zO2XMx5CwAz00-uVp68
 */

fun Route.businesses() {
    val businessService by inject<BusinessService>()

    authenticate(optional = true) {
        route("/businesses") {
            get("/") {
                val principal = call.principal<CustomerPrincipal>()
                principal ?: return@get call.respond(businessService.getActiveBusinesses().map { BusinessListDTO.fromDomain(it) })

                call.respond(businessService.getActiveBusinessesWithCustomerFavorites(principal.id).map { BusinessListDTO.fromDomain(it) })
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalStateException("Must provide id")
                val principal = call.principal<CustomerPrincipal>()

                if (principal == null) {
                    val business = businessService.getBusiness(id)
                    business ?: return@get call.respond(HttpStatusCode.NotFound)

                    return@get call.respond(BusinessDTO.fromDomain(business))
                }

                val business = businessService.getBusinessWithCustomerBonuses(id, principal.id)
                business ?: return@get call.respond(HttpStatusCode.NotFound)

                //TODO: fix image urls (retrieve from cloud storage)
                call.respond(BusinessDTO.fromDomain(business))
            }
        }
    }
}

//fun benchMark(id: Int, customerId: Int?) {
//    val businessService by inject<BusinessService>()
//    var joinTime = 0L
//    var multiQueryTime = 0L
//
//    var joinBusiness : Business? = null
//    var multiBusiness : Business? = null
//    for (i in 1..100) {
//        joinTime += measureTimeMillis { joinBusiness = businessService.getBusinessById(id, customerId) }
//        multiQueryTime += measureTimeMillis { multiBusiness = businessService.getBusinessMultiSelect(id, customerId)}
//    }
//
//    println("JOIN $joinTime")
//    println("MULTI $multiQueryTime")
//}