package com.makarytskyi.rentcar.model

import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Repairing")
@Document(collection = MongoRepairing.COLLECTION_NAME)
data class MongoRepairing(
    @Id
    val id: ObjectId? = null,
    val carId: ObjectId?,
    val date: Date?,
    val price: BigDecimal?,
    val status: RepairingStatus?,
) {
    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED
    }

    companion object {
        const val COLLECTION_NAME = "repairings"
    }
}
