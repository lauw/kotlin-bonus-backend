package common

import com.typesafe.config.ConfigFactory
import db.DatabaseFactory.dbQuery
import io.ktor.config.HoconApplicationConfig
import io.ktor.server.testing.TestApplicationEngine
import io.ktor.server.testing.createTestEnvironment
import kotlinx.coroutines.runBlocking
import model.Businesses
import module
import org.jetbrains.exposed.sql.deleteAll
import org.junit.jupiter.api.BeforeEach

open class ApiTest {
    init {
        if (!init) {
            engine.start(wait = false)
            engine.application.module()
            init = true
        }
    }

    companion object {
        var init = false

        val engine = TestApplicationEngine(createTestEnvironment {
            config = HoconApplicationConfig(ConfigFactory.load("application-test.conf"))
        })
    }

    @BeforeEach
    fun before() = runBlocking {
        dbQuery {
            Businesses.deleteAll()
            Unit
        }
    }
}