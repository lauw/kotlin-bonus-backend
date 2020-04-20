package model

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table


object Businesses : Table() {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val active = bool("active").default(false).index()
    val termsConditions = text("terms_and_conditions").nullable()
    val termsUrl = text("terms_uri").nullable()
    override val primaryKey = PrimaryKey(id)
}

data class Business(
        val id: Int,
        val name: String,
        val active: Boolean = false,
        var favorite: Boolean = false,
        var locations: MutableList<Location> = mutableListOf(),
        var bonuses: MutableList<Bonus> = mutableListOf(),
        var offers: MutableList<Offer> = mutableListOf()
)


fun toBusiness(row: ResultRow): Business =
        Business(
                id = row[Businesses.id],
                name = row[Businesses.name],
                active = row[Businesses.active],
                favorite = row.getOrNull(CustomerFavoriteBusinesses.businessId) != null
        )
