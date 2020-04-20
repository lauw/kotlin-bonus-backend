package model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object Bonuses : Table() {
    val id = integer("id").autoIncrement()
    val businessId = integer("business_id").references(Businesses.id, onDelete = ReferenceOption.CASCADE).index()
    val name = text("name")
    val imgUri = text("img_uri")
    val amount = short("amount")
    val rewardText = text("reward_text")
    val headerText = text("header_text").nullable()
    val active = bool("active")
    override val primaryKey = PrimaryKey(id)
}

data class Bonus(
    val id: Int,
    val businessId: Int,
    val name: String,
    val imgUri: String,
    val amount: Short,
    val rewardText: String,
    val headerText: String?,
    val active: Boolean,
    var customerCount: Short = 0
)

fun toBonus(row: ResultRow): Bonus =
        Bonus(
                id = row[Bonuses.id],
                businessId = row[Bonuses.businessId],
                name = row[Bonuses.name],
                imgUri = row[Bonuses.imgUri],
                amount = row[Bonuses.amount],
                rewardText = row[Bonuses.rewardText],
                headerText = row[Bonuses.headerText],
                active = row[Bonuses.active]
        )