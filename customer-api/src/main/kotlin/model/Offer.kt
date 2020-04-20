package model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime


object Offers : Table() {
    val id = integer("id").autoIncrement()
    val name = text("name")
    val businessId = integer("business_id").references(Businesses.id, onDelete = ReferenceOption.CASCADE).index()
    val description = text("description")
    val imageUri = text("img_uri")
    val scanRequired = bool("scan_required")
    val startDate = datetime("start_date").clientDefault { LocalDateTime.now() }
    val endDate = datetime("end_date")
    val enabled = bool("enabled").default(false)

    override val primaryKey = PrimaryKey(id)

    init {
        index(false, businessId, startDate, endDate)
        index(false, businessId, enabled, startDate, endDate)
    }
}

data class Offer(
    val id: Int,
    val name: String,
    val businessId: Int,
    val description: String,
    val imgUri: String,
    val scanRequired: Boolean,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val enabled: Boolean
)


 fun toOffer(row: ResultRow): Offer =
        Offer(
                id = row[Offers.id],
                name = row[Offers.name],
                businessId = row[Offers.businessId],
                description = row[Offers.description],
                imgUri = row[Offers.imageUri],
                scanRequired = row[Offers.scanRequired],
                startDate = row[Offers.startDate],
                endDate = row[Offers.endDate],
                enabled = row[Offers.enabled]
        )