package com.makarytskyi.rentcar.order.application.mapper

import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import java.math.BigDecimal
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
        val result = order.toResponse(car.price)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `order mapper throws IllegalArgumentException if car price is null`() {
        // GIVEN
        val order = emptyOrder()
        val price = null

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { order.toResponse(price) }
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
}
