package v1.dto

import model.Bonus
data class BonusDTO(
    val id: Int,
    val name: String,
    val amount: Short,
    val headerText: String?,
    val imgUri: String,
    val rewardText: String,
    val customerCount: Short
) {
    companion object {
        fun fromDomain(bonus: Bonus): BonusDTO =
                BonusDTO(bonus.id, bonus.name, bonus.amount, bonus.headerText, bonus.imgUri, bonus.rewardText, bonus.customerCount)
    }
}