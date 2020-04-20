package db

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.config.HoconApplicationConfig
import kotlinx.coroutines.Dispatchers
import model.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.system.measureTimeMillis

object DatabaseFactory {
    private val appConfig = HoconApplicationConfig(ConfigFactory.load())
    private val dbUrl = appConfig.property("db.jdbcUrl").getString()
    private val dbUser = appConfig.property("db.dbUser").getString()
    private val dbPassword = appConfig.property("db.dbPassword").getString()

    fun init(test: Boolean = false) {
        if (test) {
            Database.connect(hikariH2())
            createSchema()
            return
        }

        Database.connect(hikariPostgresql())
        createSchema()

        val seedTime = measureTimeMillis {
            seedDb()
        }

        println("DB seeded in $seedTime ms")
    }

    private fun hikariH2(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = "org.h2.Driver"
        config.jdbcUrl = "jdbc:h2:mem:test"
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    private fun hikariPostgresql(): HikariDataSource {
        val config = HikariConfig()

        config.addDataSourceProperty("reWriteBatchedInserts", "true")

        config.driverClassName = "org.postgresql.Driver"
        config.jdbcUrl = dbUrl
        config.username = dbUser
        config.password = dbPassword
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(
            block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }

    private fun createSchema() {
        transaction {
            create(Businesses)
            create(Locations)
            create(Customers)
            create(CustomerFavoriteBusinesses)
            create(Offers)
            create(PromotedOffers)
            create(Bonuses)
            create(CustomerBonuses)
        }
    }

    private fun seedDb() {
        var seeded = false
        transaction {
            if (Businesses.selectAll().count() > 0) {
                seeded = true // already seeded
            }
        }

        if (seeded) {
            return
        }

        for (i in 1..40) {
            transaction {
                Businesses.insert {
                    it[id] = i
                    it[name] = "Business-$i"
                    it[active] = true
                }

                Customers.insert {
                    it[id] = i
                    it[firebaseUid] = "test-id-$i"
                    it[emailAddress] = "test-email-$i"
                }

                CustomerFavoriteBusinesses.insert {
                    it[customerId] = i
                    it[businessId] = i
                }

                Offers.insert {
                    it[id] = i
                    it[businessId] = i
                    it[name] = "testOffer $i"
                    it[description] = "testOffer $i"
                    it[imageUri] = "testImage $i"
                    it[scanRequired] = true
                    it[startDate] = LocalDateTime.now(ZoneOffset.UTC).minusHours(1)
                    it[endDate] = LocalDateTime.now(ZoneOffset.UTC).plusHours(25)
                    it[enabled] = true
                }

                PromotedOffers.insert {
                    it[id] = i
                    it[offerId] = i
                    it[startDate] = LocalDateTime.now(ZoneOffset.UTC).minusHours(1)
                    it[endDate] = LocalDateTime.now(ZoneOffset.UTC).plusHours(25)
                }

                Bonuses.insert {
                    it[id] = i
                    it[name] = "bonus $i"
                    it[businessId] = i
                    it[imgUri] = "test $i"
                    it[amount] = 10
                    it[rewardText] = "Hello $i"
                    it[active] = true
                }

                Locations.insert {
                    it[id] = i
                    it[businessId] = i
                    it[name] = "test $i"
                    it[address] = "testi"
                    it[latitude] = 1
                    it[longitude] = 1
                    it[city] = "testi"
                    it[imgUri] = "testi"
                }

                CustomerBonuses.insert {
                    it[customerId] = i
                    it[bonusId] = i
                    it[count] = 3
                }
            }
        }
    }
}