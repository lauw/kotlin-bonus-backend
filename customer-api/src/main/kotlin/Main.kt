import auth.CustomerPrincipal
import auth.SimpleJWT
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import db.DatabaseFactory
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.jwt.jwt
import io.ktor.features.*
import io.ktor.jackson.jackson
import io.ktor.routing.Routing
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import v1.v1
import java.text.SimpleDateFormat

fun Application.module() {
    var environmentStr = environment.config.property("ktor.deployment.environment").getString()
    val isTestEnvironment = environmentStr == "test"

    install(DefaultHeaders)
    install(CallLogging) {
        level = Level.DEBUG
    }

    install(Koin) {
        getModules(environment)
        slf4jLogger()
    }

    install(Compression)
    install(ContentNegotiation) {
        jackson {
            dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm");
            configure(SerializationFeature.INDENT_OUTPUT, true)
            configure(SerializationFeature.WRITE_ENUMS_USING_INDEX, true)
            registerModule(JavaTimeModule())
        }
    }

    install(CORS) {
        anyHost() // #yolo
    }

    DatabaseFactory.init(isTestEnvironment)

    if (!isTestEnvironment) {
        val options = FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()

        FirebaseApp.initializeApp(options)
    }

    val simpleJWT by inject<SimpleJWT>()


    val jwtAudience = environment.config.property("jwt.audience").toString()
    val jwtRealm = environment.config.property("jwt.realm").toString()

    install(Authentication) {
        jwt {
            realm = jwtRealm
            verifier(simpleJWT.verifier)
            validate {
                if (it.payload.audience.contains(jwtAudience)) {
                    val id = it.payload.getClaim("id").asInt()
                    CustomerPrincipal(id)
                } else null
            }
        }
    }

    install(Routing) {
        v1()
    }

}

fun main(args: Array<String>) {
    embeddedServer(Netty, commandLineEnvironment(args)).start(wait = true)
}