package com.makarytskyi.rentcar.model

import java.util.Date
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Repairing")
@Document(collection = MongoRepairing.COLLECTION_NAME)
data class MongoRepairing(
    @Id
    val id: ObjectId? = null,
    val carId: ObjectId?,
    val date: Date?,
    val price: Int?,
    val status: RepairingStatus?,
) {
    companion object {
        const val COLLECTION_NAME = "repairings"
    }

    enum class RepairingStatus {
        PENDING, IN_PROGRESS, COMPLETED;
    }
}