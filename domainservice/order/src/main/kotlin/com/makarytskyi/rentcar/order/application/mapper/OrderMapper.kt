package com.makarytskyi.rentcar.order.application.mapper

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.rentcar.common.util.Utils.dateToTimestamp
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import java.math.BigDecimal

fun DomainOrder.toResponse(price: BigDecimal?): DomainOrder {
    val bookedDays =
        from.toInstant()?.until(to.toInstant(), java.time.temporal.ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return DomainOrder(
        id = id,
        carId = carId,
        userId = userId,
        from = from,
        to = to,
        price = requireNotNull(price?.times(bookedDays)) { "Price of car is null" }
    )
}

fun AggregatedDomainOrder.toResponse(): AggregatedDomainOrder {
    val bookedDays =
        from.toInstant()?.until(to.toInstant(), java.time.temporal.ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return AggregatedDomainOrder(
        id = id,
        car = car,
        user = user,
        from = from,
        to = to,
        price = car.price.times(bookedDays),
    )
}

fun DomainOrder.toProto(): Order = Order.newBuilder()
    .also {
        it.id = id
        it.carId = carId
        it.userId = userId
        it.from = dateToTimestamp(from)
        it.to = dateToTimestamp(to)
        it.price = price?.toDouble() ?: 0.0
    }
    .build()
