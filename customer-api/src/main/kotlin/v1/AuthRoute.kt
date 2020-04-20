package v1

import auth.SimpleJWT
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseToken
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receiveText
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import model.Customer
import org.koin.ktor.ext.inject
import service.CustomerService

fun Route.auth() {
    val simpleJWT by inject<SimpleJWT>()
    val customerService by inject<CustomerService>()

    route("/auth") {
        post("/firebase") {
            val fbToken = call.receiveText()

            val decodedToken: FirebaseToken = try {
                FirebaseAuth.getInstance().verifyIdToken(fbToken)
            } catch (ex: FirebaseAuthException) {
                return@post call.respond(HttpStatusCode.BadRequest, ex.localizedMessage)
            }

            var customer = customerService.getCustomerByFirebaseUid(decodedToken.uid)

            @Suppress("UNCHECKED_CAST")
            if (customer == null) {
                val fullName = decodedToken.name
                var firstName : String? = null
                var lastName : String? = null

                val firebaseDetails = decodedToken.claims["firebase"] as Map<String, Any>
                val provider = firebaseDetails["sign_in_provider"].toString()
                val identities = firebaseDetails["identities"] as Map<String, List<String>>
                val providerId = identities[provider]?.firstOrNull()

                val spaceIndex = fullName.indexOf(" ")

                if (spaceIndex >= 0) {
                    firstName = fullName.substring(0, spaceIndex).trim()
                    lastName = fullName.substring(spaceIndex).trim()
                }

                val customerId = customerService.addCustomer(decodedToken.uid, decodedToken.email, firstName, lastName, provider, providerId)
                customer = Customer(customerId, decodedToken.uid, decodedToken.email)
            }

            println("ISSUEING TOKEN FOR USER WITH ID ${customer.id}")
            val token = simpleJWT.sign(customer.id)
            call.respond(token)
        }
    }
}
