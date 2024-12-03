package com.makarytskyi.rentcar.order.application.mapper

import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.emptyOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomAggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomCity
import com.makarytskyi.rentcar.fixtures.UserFixture.randomName
import com.makarytskyi.rentcar.fixtures.UserFixture.randomPhoneNumber
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.order.domain.patch.DomainOrderPatch
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.toProto
import com.makarytskyi.rentcar.user.domain.patch.DomainUserPatch
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

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
        val price = BigDecimal.ZERO

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

    @Test
    fun `patch mapper should return order with updated fields`() {
        // GIVEN
        val order = randomOrder(ObjectId().toString(), ObjectId().toString())

        val patch = DomainOrderPatch(
            from = monthAfter,
            to = monthAndDayAfter,
        )

        val expected = order.copy(from = patch.from!!, to = patch.to!!)

        // WHEN
        val result = order.fromPatch(patch)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `patch mapper should return old order if patch is empty`() {
        // GIVEN
        val order = randomOrder(ObjectId().toString(), ObjectId().toString())

        val patch = DomainOrderPatch(
            from = null,
            to = null,
        )

        // WHEN
        val result = order.fromPatch(patch)

        // THEN
        assertEquals(order, result)
    }

    @ParameterizedTest
    @MethodSource("colorMapperProto")
    fun `proto mapper should return corresponding color`(
        domainColor: DomainCar.CarColor,
        protoColor: CarColor,
    ) {
        // WHEN
        val result = domainColor.toProto()

        // THEN
        assertEquals(protoColor, result)
    }

    companion object {
        @JvmStatic
        fun colorMapperProto(): List<Arguments> {
            return DomainCar.CarColor.entries
                .map {
                    val expected: CarColor = when (it) {
                        DomainCar.CarColor.RED -> CarColor.CAR_COLOR_RED
                        DomainCar.CarColor.GREEN -> CarColor.CAR_COLOR_GREEN
                        DomainCar.CarColor.BLUE -> CarColor.CAR_COLOR_BLUE
                        DomainCar.CarColor.BLACK -> CarColor.CAR_COLOR_BLACK
                        DomainCar.CarColor.WHITE -> CarColor.CAR_COLOR_WHITE
                        DomainCar.CarColor.GREY -> CarColor.CAR_COLOR_GREY
                        DomainCar.CarColor.YELLOW -> CarColor.CAR_COLOR_YELLOW
                        DomainCar.CarColor.UNSPECIFIED -> CarColor.CAR_COLOR_UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
