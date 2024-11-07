package com.makarytskyi.rentcar.dto.repairing

import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId

data class CreateRepairingRequest(
    @field:NotBlank
    val carId: String,
    val date: Date?,
    @field:Min(0)
    val price: BigDecimal?,
    val status: RepairingStatus?,
) {

    companion object {
        fun toEntity(repairingRequest: CreateRepairingRequest): MongoRepairing = MongoRepairing(
            carId = ObjectId(repairingRequest.carId),
            date = repairingRequest.date,
            price = repairingRequest.price,
            status = repairingRequest.status,
        )
    }
}
