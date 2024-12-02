package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.common.util.Utils.dateToTimestamp
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.Utils.getDateFromNow
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.patch.DomainOrderPatch
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch
import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId

object OrderFixture {
    var yesterday = getDateFromNow(-1)
    var tomorrow = getDateFromNow(1)
    var twoDaysAfter = getDateFromNow(2)
    var threeDaysAfter = getDateFromNow(3)
    var monthAfter = getDateFromNow(30)
    var monthAndDayAfter = getDateFromNow(31)

    fun randomOrder(carId: String?, userId: String?) = DomainOrder(
        id = ObjectId().toString(),
        carId = carId!!,
        userId = userId!!,
        from = tomorrow,
        to = twoDaysAfter,
        price = randomPrice(),
    )

    fun emptyOrder() = DomainOrder(
        id = null,
        carId = "",
        userId = "",
        from = Date(),
        to = Date(),
        price = BigDecimal.ZERO
    )

    fun emptyOrderPatch() = DomainOrderPatch(
        from = null,
        to = null,
    )

    fun randomAggregatedOrder(car: DomainCar, user: DomainUser) = AggregatedDomainOrder(
        id = ObjectId().toString(),
        car = car,
        user = user,
        from = tomorrow,
        to = twoDaysAfter,
        price = car.price,
    )

    fun aggregatedOrder(order: DomainOrder, car: DomainCar, user: DomainUser) = AggregatedDomainOrder(
        id = order.id,
        car = car,
        user = user,
        from = order.from,
        to = order.to,
        price = car.price,
    )

    fun responseOrderDto(mongoOrder: DomainOrder, mongoCar: DomainCar) = DomainOrder(
        id = mongoOrder.id.toString(),
        carId = mongoOrder.carId,
        userId = mongoOrder.userId,
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price,
    )

    fun responseAggregatedOrderDto(mongoOrder: AggregatedDomainOrder, mongoCar: DomainCar) = AggregatedDomainOrder(
        id = mongoOrder.id.toString(),
        car = mongoOrder.car,
        user = mongoOrder.user,
        from = mongoOrder.from,
        to = mongoOrder.to,
        price = mongoCar.price,
    )

    fun createOrderRequestDto(mongoCar: DomainCar, mongoUser: DomainUser) = DomainOrder(
        id = null,
        carId = mongoCar.id.toString(),
        userId = mongoUser.id.toString(),
        from = monthAfter,
        to = monthAndDayAfter,
        price = null,
    )

    fun createdOrder(mongoOrder: DomainOrder) = mongoOrder.copy(id = ObjectId().toString())

    fun updateOrderRequestDto() = DomainOrderPatch(
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun domainOrderPatch(patch: DomainOrderPatch, oldOrder: DomainOrder) = oldOrder.copy(
        from = patch.from ?: oldOrder.from,
        to = patch.to ?: oldOrder.to,
    )

    fun domainOrderPatch(patch: DomainOrderPatch, oldOrder: AggregatedDomainOrder): DomainOrder = DomainOrder(
        id = oldOrder.id,
        carId = oldOrder.car.id.toString(),
        userId = oldOrder.user.id.toString(),
        from = patch.from ?: oldOrder.from,
        to = patch.to ?: oldOrder.to,
        price = oldOrder.price,
    )

    fun updatedOrder(oldMongoOrder: AggregatedDomainOrder, request: DomainOrderPatch) =
        DomainOrder(
            id = oldMongoOrder.id,
            carId = oldMongoOrder.car.toString(),
            userId = oldMongoOrder.user.toString(),
            from = request.from ?: oldMongoOrder.from,
            to = request.to ?: oldMongoOrder.to,
            price = oldMongoOrder.price,
        )

    fun updatedOrder(oldMongoOrder: DomainOrder, request: DomainOrderPatch) =
        DomainOrder(
            id = oldMongoOrder.id,
            carId = oldMongoOrder.carId,
            userId = oldMongoOrder.userId,
            from = request.from ?: oldMongoOrder.from,
            to = request.to ?: oldMongoOrder.to,
            price = oldMongoOrder.price,
        )

    fun orderProto(order: DomainOrder, price: Double): Order = Order
        .newBuilder().also {
            it.id = order.id.toString()
            it.carId = order.carId
            it.userId = order.userId
            it.from = dateToTimestamp(order.from)
            it.to = dateToTimestamp(order.to)
            it.price = price
        }.build()
}
