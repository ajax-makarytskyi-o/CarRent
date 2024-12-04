package com.makarytskyi.rentcar.order.domain

import com.makarytskyi.rentcar.order.domain.patch.PatchOrder
import java.math.BigDecimal
import java.util.Date

data class DomainOrder(
    val id: String,
    val carId: String,
    val userId: String,
    val from: Date,
    val to: Date,
    val price: BigDecimal?,
) {
    fun fromPatch(patch: PatchOrder): DomainOrder = this.copy(
        from = patch.from ?: this.from,
        to = patch.to ?: this.to,
    )

    fun totalPrice(carPrice: BigDecimal): BigDecimal {
        val bookedDays = from.toInstant().until(to.toInstant(), java.time.temporal.ChronoUnit.DAYS).toBigDecimal()
        return bookedDays.times(carPrice)
    }
}
