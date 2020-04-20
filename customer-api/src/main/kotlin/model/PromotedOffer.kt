package model
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.`java-time`.datetime
import java.time.LocalDateTime

object PromotedOffers: Table() {
    val id = integer("id").autoIncrement()
    val offerId = integer("offer_id").references(Offers.id, onDelete = ReferenceOption.RESTRICT).index()
    val startDate = datetime("start_date")
    val endDate = datetime("end_date")

    init {
        index(false, startDate, endDate)
    }
    override val primaryKey = PrimaryKey(Offers.id)
}

data class PromotedOffer(
    val id : Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val offer: Offer
)
