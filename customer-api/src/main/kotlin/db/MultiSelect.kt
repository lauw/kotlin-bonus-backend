package db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import java.sql.ResultSet
import java.sql.Statement

private fun executeStatement(con: Connection, sql: String, vararg queryMappings: QueryMapping) {
    try {
        val stmt: Statement = con.createStatement()
        var results: Boolean = stmt.execute(sql)
        var resultSetIndex = 0

        //Loop through the available result sets.
        do {
            if (results) {
                val rs: ResultSet = stmt.resultSet

                while (rs.next()) {
                    val mapping = queryMappings[resultSetIndex]
                    mapping.mapFn(ResultRow.create(rs, mapping.columns()))
                }

                resultSetIndex++
                rs.close()
            }

            results = stmt.moreResults
        } while (results)
        stmt.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Query.mapFn(mapFn: (ResultRow) -> Unit) :QueryMapping = QueryMapping(this, mapFn)

data class QueryMapping(val query: Query, val mapFn: (ResultRow) -> Unit = {}) {
    fun rawSQL(): String {
        return query.prepareSQL(QueryBuilder(false)) + ";"
    }

    fun columns() : List<Expression<*>> {
        return query.set.fields
    }
}

data class MultiSelect(val transaction: Transaction, val queryMappings: MutableList<QueryMapping>) {
    fun add(queryMapping: QueryMapping) {
        queryMappings.add(queryMapping)
    }

    fun execute() {
        val conn = transaction.connection.connection as Connection
        val supportsMultiQuery = TransactionManager.current().db.supportsMultipleResultSets

        if (supportsMultiQuery) {
            executeStatement(conn, queryMappings.joinToString("") { it.rawSQL() }, *queryMappings.toTypedArray())
            return
        }

        //if we dont support multiQuery, we just run them individually
        queryMappings.forEach {
            executeStatement(conn, it.rawSQL(), it)
        }
    }
}

fun Transaction.multiSelect(vararg queryMappings: QueryMapping) : MultiSelect {
    return MultiSelect(this, queryMappings.toMutableList())
}


/*
SAMPLE: IF JOIN results in only 1 row, it performs about 10% worse, if join results in a lot of rows (e.g 500), it performs a lot better

private suspend fun getBusinessMultiSelect(id: Int, customerId: Int?): Business? = DatabaseFactory.dbQuery {
    var business: Business? = null

    val multiSelect = TransactionManager.current().multiSelect(
            Businesses.select { Businesses.id eq id }.withMapFn { business = toBusiness(it) },
            Offers.select { Offers.businessId eq id }.withMapFn { business!!.offers.add(toOffer(it)) },
            Bonuses.select { Bonuses.businessId eq id }.withMapFn { business!!.bonuses.add(toBonus(it)) },
            Locations.select { Locations.businessId eq id }.withMapFn { business!!.locations.add(toLocation(it)) }
    )

    customerId?.let {
        multiSelect.add(
                CustomerBonuses.leftJoin(Bonuses)
                        .slice(CustomerBonuses.columns).select { CustomerBonuses.customerId eq customerId }
                        .withMapFn { row ->
                            val bonusId = row[CustomerBonuses.bonusId]
                            val count = row[CustomerBonuses.count]
                            business!!.bonuses.single { it.id == bonusId }.customerCount = count
                        }
        )
    }

    multiSelect.execute()
    business
}

 */