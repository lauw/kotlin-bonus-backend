package model
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Locations : Table() {
    val id = integer("id").autoIncrement()
    val businessId = integer("business_id").references(Businesses.id, onDelete = ReferenceOption.CASCADE).index()
    val name = text("name")
    val address = text("address")
    val latitude = integer("latitude")
    val longitude = integer("longitude")
    val city = varchar("city", 100).index()
    val imgUri = text("img_uri")
    override val primaryKey = PrimaryKey(Businesses.id)

    init {
        index(false, latitude, longitude)
    }
}

data class Location(
        val id: Int,
        val businessId: Int,
        val name: String,
        val address: String,
        val latitude: Int,
        val longitude: Int,
        val city: String,
        val imgUri: String
)

fun toLocation(row: ResultRow): Location =
        Location(
                id = row[Locations.id],
                businessId = row[Locations.businessId],
                name = row[Locations.name],
                address = row[Locations.address],
                latitude = row[Locations.latitude],
                longitude = row[Locations.longitude],
                city = row[Locations.city],
                imgUri = row[Locations.imgUri]
        )

