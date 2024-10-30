package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.rentcar.fixtures.Utils.getDateFromNow
import com.makarytskyi.rentcar.mapper.toResponse
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.MongoUser
import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import java.math.BigDecimal
import java.util.Date
import org.bson.types.ObjectId

object OrderFixture {
    var yesterday = getDateFromNow(-1)
    var tomorrow = getDateFromNow(1)
    var twoDaysAfter = getDateFromNow(2)
    var monthAfter = getDateFromNow(30)
    var monthAndDayAfter = getDateFromNow(31)

    fun randomOrder(carId: ObjectId?, userId: ObjectId?) = MongoOrder(
        id = ObjectId(),
        carId = carId,
        userId = userId,
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun emptyOrder() = MongoOrder(
        id = null,
        carId = null,
        userId = null,
        from = null,
        to = null,
    )

    fun emptyOrderPatch() = MongoOrderPatch(
        from = null,
        to = null,
    )

    fun randomAggregatedOrder(car: MongoCar, user: MongoUser) = AggregatedMongoOrder(
        id = ObjectId(),
        car = car,
        user = user,
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun emptyAggregatedOrder() = AggregatedMongoOrder(
        id = null,
        car = null,
        user = null,
        from = null,
        to = null,
    )

    fun aggregatedOrder(order: MongoOrder, car: MongoCar, user: MongoUser) = AggregatedMongoOrder(
        id = order.id,
        car = car,
        user = user,
        from = order.from,
        to = order.to,
    )

    fun responseOrderDto(mongoOrder: MongoOrder, mongoCar: MongoCar) = OrderResponseDto(
        id = mongoOrder.id.toString(),
        carId = mongoOrder.carId.toString(),
        userId = mongoOrder.userId.toString(),
        from = mongoOrder.from!!,
        to = mongoOrder.to!!,
        price = mongoCar.price!!,
    )

    fun emptyResponseOrderDto() = OrderResponseDto(
        id = "",
        carId = "",
        userId = "",
        from = Date(),
        to = Date(),
        price = BigDecimal.ZERO,
    )

    fun responseAggregatedOrderDto(mongoOrder: AggregatedMongoOrder, mongoCar: MongoCar) = AggregatedOrderResponseDto(
        id = mongoOrder.id.toString(),
        car = mongoOrder.car!!.toResponse(),
        user = mongoOrder.user!!.toResponse(),
        from = mongoOrder.from!!,
        to = mongoOrder.to!!,
        price = mongoCar.price!!,
    )

    fun emptyResponseAggregatedOrderDto() = AggregatedOrderResponseDto(
        id = "",
        car = MongoCar().toResponse(),
        user = MongoUser().toResponse(),
        from = Date(),
        to = Date(),
        price = BigDecimal.ZERO,
    )

    fun createOrderRequestDto(mongoCar: MongoCar, mongoUser: MongoUser) = CreateOrderRequestDto(
        carId = mongoCar.id.toString(),
        userId = mongoUser.id.toString(),
        from = monthAfter,
        to = monthAndDayAfter,
    )

    fun createOrderEntity(request: CreateOrderRequestDto) = MongoOrder(
        id = null,
        carId = ObjectId(request.carId),
        userId = ObjectId(request.userId),
        from = request.from,
        to = request.to,
    )

    fun createdOrder(mongoOrder: MongoOrder) = mongoOrder.copy(id = ObjectId())

    fun updateOrderRequestDto() = UpdateOrderRequestDto(
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun orderPatch(request: UpdateOrderRequestDto) = MongoOrderPatch(
        from = request.from,
        to = request.to,
    )

    fun updatedOrder(oldMongoOrder: AggregatedMongoOrder, request: UpdateOrderRequestDto) =
        MongoOrder(
            id = oldMongoOrder.id,
            carId = oldMongoOrder.car?.id,
            userId = oldMongoOrder.user?.id,
            from = request.from ?: oldMongoOrder.from,
            to = request.to ?: oldMongoOrder.to,
        )
}
