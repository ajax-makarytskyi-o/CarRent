package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import fixtures.CarFixture.randomCar
import fixtures.OrderFixture.createOrderEntity
import fixtures.OrderFixture.createOrderRequest
import fixtures.OrderFixture.emptyAggregatedOrder
import fixtures.OrderFixture.emptyOrder
import fixtures.OrderFixture.emptyResponseAggregatedOrder
import fixtures.OrderFixture.emptyResponseOrder
import fixtures.OrderFixture.randomAggregatedOrder
import fixtures.OrderFixture.randomOrder
import fixtures.OrderFixture.responseAggregatedOrder
import fixtures.OrderFixture.responseOrder
import fixtures.OrderFixture.updateOrderEntity
import fixtures.OrderFixture.updateOrderRequest
import fixtures.UserFixture.randomUser
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
        val response = updateOrderEntity(request)

        // WHEN
        val result = UpdateOrderRequest.toEntity(request)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateOrderRequest(from = null, to = null)
        val response = emptyOrder()

        // WHEN
        val result = UpdateOrderRequest.toEntity(request)

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
