package v1

import auth.CustomerPrincipal
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import model.NotificationSetting
import org.koin.ktor.ext.inject
import service.CustomerService

data class PostStarredBusinesses(val ids: List<Int>)
data class PostNotificationSettings(val push : NotificationSetting, val email: NotificationSetting)


fun Route.customer() {
    val customerService by inject<CustomerService>()

    authenticate {
        route("/customer") {
            post("/notification") {
                val principal = call.principal<CustomerPrincipal>()!!
                val notificationSettings = call.receive<PostNotificationSettings>()

                call.respond(HttpStatusCode.Accepted)

                customerService.saveNotificationSettings(principal.id, notificationSettings.push, notificationSettings.email)
            }

            post("/starred") {
                val principal = call.principal<CustomerPrincipal>()!!
                val starredBusinesses = call.receive<PostStarredBusinesses>()

                call.respond(HttpStatusCode.Accepted)

                customerService.updateStarredBusinesses(principal.id, starredBusinesses.ids)
            }
        }
    }
}