package v1

import io.ktor.routing.Route
import io.ktor.routing.route

fun Route.v1() {
    route("/v1") {
        auth()
        businesses()
        customer()
        offers()
    }
}