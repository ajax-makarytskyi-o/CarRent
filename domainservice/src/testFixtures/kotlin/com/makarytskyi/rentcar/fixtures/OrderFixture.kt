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

    fun responseOrderDto(order: DomainOrder, car: DomainCar) = DomainOrder(
        id = order.id.toString(),
        carId = order.carId,
        userId = order.userId,
        from = order.from,
        to = order.to,
        price = car.price,
    )

    fun responseAggregatedOrderDto(order: AggregatedDomainOrder, car: DomainCar) = AggregatedDomainOrder(
        id = order.id.toString(),
        car = order.car,
        user = order.user,
        from = order.from,
        to = order.to,
        price = car.price,
    )

    fun createOrderRequestDto(car: DomainCar, user: DomainUser) = DomainOrder(
        id = null,
        carId = car.id.toString(),
        userId = user.id.toString(),
        from = monthAfter,
        to = monthAndDayAfter,
        price = null,
    )

    fun createdOrder(order: DomainOrder) = order.copy(id = ObjectId().toString())

    fun updateOrderRequestDto() = DomainOrderPatch(
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun domainOrderPatch(patch: DomainOrderPatch, oldOrder: DomainOrder) = oldOrder.copy(
        from = patch.from ?: oldOrder.from,
        to = patch.to ?: oldOrder.to,
    )

    fun updatedOrder(oldOrder: DomainOrder, request: DomainOrderPatch) =
        DomainOrder(
            id = oldOrder.id,
            carId = oldOrder.carId,
            userId = oldOrder.userId,
            from = request.from ?: oldOrder.from,
            to = request.to ?: oldOrder.to,
            price = oldOrder.price,
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
