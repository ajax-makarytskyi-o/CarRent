package com.makarytskyi.rentcar.order.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.create.CreateOrder
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
        car = requireNotNull(this.car?.toDomain()) { "Car is null" },
        user = requireNotNull(this.user?.toDomain()) { "User is null" },
        from = requireNotNull(this.from) { "Start date of order is null" },
        to = requireNotNull(this.to) { "End date of order is null" },
        price = requireNotNull(car?.price?.times(bookedDays)) { "Price of car is null" },
    )
}

fun MongoOrder.toDomain(): DomainOrder {
    return DomainOrder(
        id = this.id.toString(),
        carId = this.carId.toString(),
        userId = this.userId.toString(),
        from = requireNotNull(this.from) { "Start date of order is null" },
        to = requireNotNull(this.to) { "End date of order is null" },
        price = null,
    )
}

fun CreateOrder.toMongo(): MongoOrder {
    return MongoOrder(
        carId = ObjectId(this.carId),
        userId = ObjectId(this.userId),
        from = this.from,
        to = this.to
    )
}
