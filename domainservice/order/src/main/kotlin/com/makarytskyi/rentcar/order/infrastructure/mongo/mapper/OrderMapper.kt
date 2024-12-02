package com.makarytskyi.rentcar.order.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import com.makarytskyi.rentcar.order.infrastructure.mongo.entity.MongoOrder
import com.makarytskyi.rentcar.order.infrastructure.mongo.entity.projection.AggregatedMongoOrder
import com.makarytskyi.rentcar.user.infrastructure.mongo.mapper.toDomain
import java.math.BigDecimal
import org.bson.types.ObjectId

@Suppress("ThrowsCount")
fun AggregatedMongoOrder.toDomain(): AggregatedDomainOrder {
    val bookedDays =
        from?.toInstant()?.until(to?.toInstant(), java.time.temporal.ChronoUnit.DAYS)?.toBigDecimal() ?: BigDecimal.ZERO

    return AggregatedDomainOrder(
        id = this.id.toString(),
        car = this.car?.toDomain() ?: throw IllegalArgumentException("Car is null"),
        user = this.user?.toDomain() ?: throw IllegalArgumentException("User is null"),
        from = this.from ?: throw IllegalArgumentException("Start date of order is null"),
        to = this.to ?: throw IllegalArgumentException("End date of order is null"),
        price = car.price?.times(bookedDays) ?: throw IllegalArgumentException("Price of car is null"),
    )
}

fun MongoOrder.toDomain(): DomainOrder {
    return DomainOrder(
        id = this.id.toString(),
        carId = this.carId.toString(),
        userId = this.userId.toString(),
        from = this.from ?: throw IllegalArgumentException("Start date of order is null"),
        to = this.to ?: throw IllegalArgumentException("End date of order is null"),
        price = null,
    )
}

fun DomainOrder.toMongo(): MongoOrder {
    return MongoOrder(
        id = this.id?.let { ObjectId(it) },
        carId = ObjectId(this.carId),
        userId = ObjectId(this.userId),
        from = this.from,
        to = this.to
    )
}
