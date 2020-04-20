package service

import db.DatabaseFactory.dbQuery
import model.Offers
import model.PromotedOffer
import model.PromotedOffers
import model.toOffer
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.`java-time`.CurrentDateTime
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select

class OfferService {
    suspend fun getPromotedOffers() : List<PromotedOffer> = dbQuery {
        PromotedOffers.innerJoin(Offers)
                .select { (PromotedOffers.endDate greaterEq CurrentDateTime()) and (PromotedOffers.startDate lessEq CurrentDateTime())  }
                .map { toPromotedOffer(it) }
    }

    private fun toPromotedOffer(row: ResultRow): PromotedOffer =
            PromotedOffer(
                    id = row[PromotedOffers.id],
                    startDate = row[PromotedOffers.startDate],
                    endDate = row[PromotedOffers.endDate],
                    offer = toOffer(row)
            )

}