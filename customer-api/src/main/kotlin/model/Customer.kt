package model

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table


object Customers : Table() {
    val id = integer("id").autoIncrement()
    val firebaseUid = text("firebase_uid").uniqueIndex()
    val emailAddress = text("email_address").uniqueIndex()
    val firstName = text("first_name").nullable()
    val lastName = text("last_name").nullable()
    val provider = text("provider")
    val providerUid = text("provider_uid").nullable()
    val pushNotifications = enumeration("push_notification_setting", NotificationSetting::class).clientDefault { NotificationSetting.STARRED }
    val emailNotifications = enumeration("email_notification_setting", NotificationSetting::class).clientDefault { NotificationSetting.STARRED }
    override val primaryKey = PrimaryKey(id)
}

data class Customer (
    val id: Int,
    val firebaseUid: String,
    val emailAddress: String
)

object CustomerFavoriteBusinesses : Table() {
    val customerId = integer("customer_id").references(Customers.id, onDelete = ReferenceOption.CASCADE)
    val businessId = integer("business_id").references(Businesses.id, onDelete = ReferenceOption.CASCADE)

    init {
        index(true, customerId, businessId)
    }
}

object CustomerBonuses : Table() {
    val customerId = integer("customer_id").references(Customers.id, onDelete = ReferenceOption.CASCADE)
    val bonusId = integer("bonus_id").references(Bonuses.id, onDelete = ReferenceOption.NO_ACTION)
    val count = short("count")

    init {
        index(true, customerId, bonusId)
    }
}

enum class NotificationSetting {
    NONE,
    STARRED
}

object CustomerNotificationPreferences : Table() {
    val customerId = integer("customer_id").references(Customers.id, onDelete = ReferenceOption.CASCADE)
    val businessId = integer("business_id").references(Businesses.id, onDelete = ReferenceOption.CASCADE)
    val pushNotifications = Customers.bool("push").default(true)
    val emailNotifications = Customers.bool("email").default(true)

    init {
        index(true, customerId, businessId)
    }
}