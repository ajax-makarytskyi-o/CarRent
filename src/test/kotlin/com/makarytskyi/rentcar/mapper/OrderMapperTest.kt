package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import fixtures.CarFixture.randomCar
import fixtures.OrderFixture.createOrderRequest
import fixtures.OrderFixture.randomAggregatedOrder
import fixtures.OrderFixture.randomOrder
import fixtures.OrderFixture.updateOrderRequest
import fixtures.UserFixture.randomUser
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId

class OrderDTOTests {
    @Test
    fun `order mapper should return response successfully`() {
        // GIVEN
        val price = 300L
        val carId = ObjectId()
        val userId = ObjectId()
        val order = randomOrder(carId, userId)
        val response = OrderResponse(
            id = order.id.toString(),
            carId = carId.toString(),
            userId = userId.toString(),
            from = order.from,
            to = order.to,
            price = price,
        )

        // WHEN
        val result = OrderResponse.from(order, price.toInt())

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyCar = MongoCar(
            id = null,
            brand = null,
            model = null,
            price = null,
            year = null,
            plate = null,
            color = null,
        )

        val response = CarResponse(
            id = "",
            brand = "",
            model = "",
            price = 0,
            year = null,
            plate = "",
            color = null,
        )

        // WHEN
        val result = CarResponse.from(emptyCar)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `create request return entity successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        val response = MongoOrder(
            id = null,
            carId = ObjectId(request.carId),
            userId = ObjectId(request.userId),
            from = request.from,
            to = request.to,
        )

        // WHEN
        val result = CreateOrderRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateOrderRequest()
        val response = MongoOrder(
            id = null,
            carId = null,
            userId = null,
            from = request.from,
            to = request.to,
        )

        // WHEN
        val result = UpdateOrderRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateOrderRequest(
            from = null,
            to = null
        )
        val response = MongoOrder(
            id = null,
            carId = null,
            userId = null,
            from = null,
            to = null,
        )

        // WHEN
        val result = UpdateOrderRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `aggregated order mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)

        val response = AggregatedOrderResponse(
            id = order.id.toString(),
            car = CarResponse.from(car),
            user = UserResponse.from(user),
            from = order.from,
            to = order.to,
            price = car.price?.toLong(),
        )

        // WHEN
        val result = AggregatedOrderResponse.from(order)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `aggregated order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyOrder = AggregatedMongoOrder(
            id = null,
            car = null,
            user = null,
            from = null,
            to = null,
        )

        val response = AggregatedOrderResponse(
            id = "",
            car = null,
            user = null,
            from = null,
            to = null,
            price = 0,
        )

        // WHEN
        val result = AggregatedOrderResponse.from(emptyOrder)

        // THEN
        assertEquals(result, response)
    }
}
