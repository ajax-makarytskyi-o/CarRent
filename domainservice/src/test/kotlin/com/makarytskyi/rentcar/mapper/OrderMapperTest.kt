package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.internalapi.model.order.AggregatedOrder
import com.makarytskyi.internalapi.model.order.Order
import com.makarytskyi.internalapi.model.order.Patch
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderEntity
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyResponseAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyResponseOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.orderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.updateOrderRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.util.dateToTimestamp
import com.makarytskyi.rentcar.util.timestampToDate
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

class OrderMapperTest {
    @Test
    fun `order mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomOrder(car.id, user.id)
        val response = responseOrder(order, car)

        // WHEN
        val result = order.toResponse(car.price!!)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val order = emptyOrder()
        val response = emptyResponseOrder()

        // WHEN
        val result = order.toResponse(null)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create request return entity successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequest(car, user)
        val response = createOrderEntity(request)

        // WHEN
        val result = request.toEntity()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateOrderRequest()
        val response = orderPatch(request)

        // WHEN
        val result = request.toPatch()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateOrderRequest(from = null, to = null)
        val response = emptyOrderPatch()

        // WHEN
        val result = request.toPatch()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `aggregated order mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomAggregatedOrder(car, user)
        val response = responseAggregatedOrder(order, car)

        // WHEN
        val result = order.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `aggregated order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyOrder = emptyAggregatedOrder()
        val response = emptyResponseAggregatedOrder()

        // WHEN
        val result = emptyOrder.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create order proto request mapper return create request dto`() {
        // GIVEN
        val emptyOrder = emptyAggregatedOrder()
        val response = emptyResponseAggregatedOrder()

        // WHEN
        val result = emptyOrder.toResponse()

        // THEN
        assertEquals(response, result)
    }
}
