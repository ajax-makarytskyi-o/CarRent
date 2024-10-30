package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderEntity
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequestDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.orderPatch
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.updateOrderRequestDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows

class OrderMapperTest {
    @Test
    fun `order mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val order = randomOrder(car.id, user.id)
        val response = responseOrderDto(order, car)

        // WHEN
        val result = order.toResponse(car.price!!)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `order mapper throws IllegalArgumentException if car date fields are null`() {
        // GIVEN
        val order = emptyOrder()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { order.toResponse(null) }
    }

    @Test
    fun `create request return entity successfully`() {
        // GIVEN
        val car = randomCar()
        val user = randomUser()
        val request = createOrderRequestDto(car, user)
        val response = createOrderEntity(request)

        // WHEN
        val result = request.toEntity()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateOrderRequestDto()
        val response = orderPatch(request)

        // WHEN
        val result = request.toPatch()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateOrderRequestDto(from = null, to = null)
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
        val response = responseAggregatedOrderDto(order, car)

        // WHEN
        val result = order.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `aggregated order mapper throws IllegalArgumentException if date fields are null`() {
        // GIVEN
        val emptyOrder = emptyAggregatedOrder()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { emptyOrder.toResponse() }
    }

    @Test
    fun `create order proto request mapper throws IllegalArgumentException if dates are nullable`() {
        // GIVEN
        val emptyOrder = emptyAggregatedOrder()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { emptyOrder.toResponse() }
    }
}
