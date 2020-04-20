package service

import db.DatabaseFactory.dbQuery
import model.Customer
import model.CustomerFavoriteBusinesses
import model.Customers
import model.NotificationSetting
import org.jetbrains.exposed.sql.*

class CustomerService {
    suspend fun getCustomerByFirebaseUid(firebaseUid: String) : Customer? = dbQuery {
        Customers.select { Customers.firebaseUid eq firebaseUid }
            .mapNotNull { toCustomer(it) }
            .singleOrNull()
    }

    suspend fun addCustomer(firebaseUid: String, emailAddress: String, firstName: String?, lastName: String?, provider: String, providerUid: String?) : Int = dbQuery {
            Customers.insert {
                it[Customers.firebaseUid] = firebaseUid
                it[Customers.emailAddress] = emailAddress
                it[Customers.firstName] = firstName
                it[Customers.lastName] = lastName
                it[Customers.provider] = provider
                it[Customers.providerUid] = providerUid
            } get Customers.id
    }

    suspend fun updateStarredBusinesses(customerId: Int, businessIds: List<Int>) = dbQuery {
        CustomerFavoriteBusinesses.deleteWhere { CustomerFavoriteBusinesses.customerId eq customerId }
        CustomerFavoriteBusinesses.batchInsert(businessIds) { businessId ->
            this[CustomerFavoriteBusinesses.customerId] = customerId
            this[CustomerFavoriteBusinesses.businessId] = businessId
        }
    }

    suspend fun saveNotificationSettings(customerId: Int, pushSetting: NotificationSetting, emailSetting: NotificationSetting) = dbQuery {
        Customers.update({ Customers.id eq customerId }) {
            it[pushNotifications] = pushSetting
            it[emailNotifications] = emailSetting
        }
    }

    private fun toCustomer(row: ResultRow): Customer =
            Customer(
                    id = row[Customers.id],
                    firebaseUid = row[Customers.firebaseUid],
                    emailAddress = row[Customers.emailAddress]
            )
}
