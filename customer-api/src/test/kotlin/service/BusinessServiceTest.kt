package service

import common.ApiTest
import db.DatabaseFactory.dbQuery
import kotlinx.coroutines.runBlocking
import model.Businesses
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.insert
import org.junit.jupiter.api.Test

class BusinessServiceTest: ApiTest() {
    private val businessService = BusinessService()

    @Test
    fun testGetBusinesses() = runBlocking {
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

        val businesses = businessService.getActiveBusinesses()

        assertThat(businesses).hasSize(2)
        assertThat(businesses).extracting("name").containsExactlyInAnyOrder("Business-1", "Business-2")

        Unit
    }

    @Test
    fun testGetBusiness() = runBlocking {
        dbQuery {
            Businesses.insert {
                it[id] = 1
                it[name] = "Business-1"
                it[active] = true
            }
        }

        val business = businessService.getBusiness(1)

        assertThat(business).isNotNull
        assertThat(business).extracting("id").isEqualTo(1)

        Unit
    }

}