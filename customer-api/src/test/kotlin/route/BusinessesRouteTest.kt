package route

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import common.ApiTest
import db.DatabaseFactory.dbQuery
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import kotlinx.coroutines.runBlocking
import model.Bonuses
import model.Businesses
import org.jetbrains.exposed.sql.insert
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import v1.dto.BusinessDTO
import v1.dto.BusinessListDTO

class BusinessesRouteTest: ApiTest() {
    @Test
    fun testListBusinesses() = with(engine) {
        runBlocking {
            dbQuery {
                Businesses.insert {
                    it[name] = "Business-1"
                    it[active] = true
                }

                Businesses.insert {
                    it[name] = "Business-2"
                    it[active] = true
                }
            }

            with(handleRequest(HttpMethod.Get, "/v1/businesses")) {
                assertEquals(HttpStatusCode.OK, response.status())

                val objectMapper = ObjectMapper().registerKotlinModule()
                val businesses = objectMapper.readValue<List<BusinessListDTO>>(response.content!!)

                assertEquals(2, businesses.size)
                assertEquals("Business-1", businesses.first().name)
                assertEquals("Business-2", businesses.last().name)
            }

            Unit
        }
    }

    @Test
    fun testGetBusiness() = with(engine) {
        runBlocking {
            dbQuery {
                Businesses.insert {
                    it[id] = 1
                    it[name] = "Business-1"
                    it[active] = true
                }

                Bonuses.insert {
                    it[businessId] = 1
                    it[name] = "bonus 1"
                    it[imgUri] = "test-img-1"
                    it[amount] = 10
                    it[rewardText] = "Bonus #1"
                    it[active] = true
                }
            }

            with(handleRequest(HttpMethod.Get, "/v1/businesses/1")) {
                assertEquals(HttpStatusCode.OK, response.status())

                val objectMapper = ObjectMapper().registerKotlinModule()
                val business = objectMapper.readValue<BusinessDTO>(response.content!!)

                assertEquals(1, business.id)
                assertEquals("Business-1", business.name)
                assertEquals(1, business.bonuses.size)
                assertEquals("bonus 1", business.bonuses.first().name)
            }

            Unit
        }
    }
}
