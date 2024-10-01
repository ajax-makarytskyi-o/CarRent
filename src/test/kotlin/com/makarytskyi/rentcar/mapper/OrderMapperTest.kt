package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
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
        val result = OrderResponse.from(order, car.price)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val order = emptyOrder()
        val response = emptyResponseOrder()

        // WHEN
        val result = OrderResponse.from(order, null)

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
        val result = CreateOrderRequest.toEntity(request)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateOrderRequest()
        val response = orderPatch(request)

        // WHEN
        val result = UpdateOrderRequest.toPatch(request)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateOrderRequest(from = null, to = null)
        val response = emptyOrderPatch()

        // WHEN
        val result = UpdateOrderRequest.toPatch(request)

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
        val result = AggregatedOrderResponse.from(order)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `aggregated order mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyOrder = emptyAggregatedOrder()
        val response = emptyResponseAggregatedOrder()

        // WHEN
        val result = AggregatedOrderResponse.from(emptyOrder)

        // THEN
        assertEquals(response, result)
    }
}
