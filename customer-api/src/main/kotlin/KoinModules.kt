import auth.SimpleJWT
import io.ktor.application.ApplicationEnvironment
import org.koin.core.KoinApplication
import org.koin.dsl.module
import org.koin.experimental.builder.single
import service.BusinessService
import service.CustomerService
import service.OfferService

fun KoinApplication.getModules(environment: ApplicationEnvironment) : KoinApplication {
    val authModule = module {
        val jwtIssuer = environment.config.property("jwt.domain").toString()
        val jwtAudience = environment.config.property("jwt.audience").toString()
        val jwtSecret = environment.config.property("jwt.secret").toString()

        single { SimpleJWT(jwtSecret, jwtAudience, jwtIssuer) }
    }

    val serviceModule = module {
        single<CustomerService>()
        single<BusinessService>()
        single<OfferService>()
    }

    return modules(listOf(authModule, serviceModule))
}