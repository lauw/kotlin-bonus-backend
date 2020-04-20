package service

import db.DatabaseFactory.dbQuery
import db.mapFn
import db.multiSelect
import model.*
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.TransactionManager


class BusinessService {
    suspend fun getActiveBusinesses(): List<Business> = dbQuery {
        Businesses
                .slice(Businesses.id, Businesses.name, Businesses.active)
                .select { Businesses.active eq true }
                .orderBy(Businesses.id)
                .limit(100)
                .map { toBusiness(it) }
    }

    suspend fun getActiveBusinessesWithCustomerFavorites(customerId: Int): List<Business> = dbQuery {
        Businesses
                .join(CustomerFavoriteBusinesses, JoinType.LEFT, additionalConstraint = {
                    CustomerFavoriteBusinesses.customerId eq customerId
                })
                .select { Businesses.active eq true }
                .orderBy(Businesses.id)
                .limit(100)
                .map { toBusiness(it) }
    }


    suspend fun getBusinessWithCustomerBonuses(id: Int, customerId: Int): Business? = getBusinessById(id, customerId)
    suspend fun getBusiness(id: Int): Business? = getBusinessById(id, null)

    private suspend fun getBusinessById(id: Int, customerId: Int?): Business? = dbQuery {
        lateinit var business: Business;

        val multiSelect = TransactionManager.current().multiSelect(
                Businesses.select { Businesses.id eq id }.mapFn { business = toBusiness(it) },
                Offers.select { Offers.businessId eq id and (Offers.enabled eq true) and ((Offers.endDate greaterEq CurrentDateTime()) and (Offers.startDate lessEq CurrentDateTime())) }
                        .mapFn { business.offers.add(toOffer(it)) },
                Bonuses.select { Bonuses.businessId eq id and (Bonuses.active eq true) }.mapFn { business.bonuses.add(toBonus(it)) },
                Locations.select { Locations.businessId eq id }.mapFn { business.locations.add(toLocation(it)) }
        )

        customerId?.let {
            multiSelect.add(
                    CustomerBonuses.leftJoin(Bonuses)
                            .slice(CustomerBonuses.columns).select { Bonuses.businessId eq id and (CustomerBonuses.customerId eq customerId) }
                            .mapFn { row ->
                                val bonusId = row[CustomerBonuses.bonusId]
                                val count = row[CustomerBonuses.count]
                                business.bonuses.find { b -> b.id == bonusId }?.customerCount = count
                            }
            )
        }

        multiSelect.execute()
        business
    }


}