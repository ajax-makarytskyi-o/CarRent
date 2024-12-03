package com.makarytskyi.rentcar.car.application.mapper

import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.entity.MongoCar
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toDomain
import com.makarytskyi.rentcar.car.infrastructure.mongo.mapper.toMongo
import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class CarMapperTest {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)

        // WHEN
        val result = car.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `response mapper should throw exception if id is null`() {
        // GIVEN
        val car = randomCar().copy(id = null)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { car.toResponse() }
    }

    @ParameterizedTest
    @MethodSource("colorMapperMongo")
    fun `mongo mapper should return corresponding color`(
        domainColor: DomainCar.CarColor,
        mongoColor: MongoCar.CarColor,
    ) {
        // WHEN
        val result = domainColor.toMongo()

        // THEN
        assertEquals(mongoColor, result)
    }

    @ParameterizedTest
    @MethodSource("colorMapperDomain")
    fun `domain mapper should return corresponding color`(
        mongoColor: MongoCar.CarColor,
        domainColor: DomainCar.CarColor,
    ) {
        // WHEN
        val result = mongoColor.toDomain()

        // THEN
        assertEquals(domainColor, result)
    }

    companion object {
        @JvmStatic
        fun colorMapperMongo(): List<Arguments> {
            return DomainCar.CarColor.entries
                .map {
                    val expected: MongoCar.CarColor = when (it) {
                        DomainCar.CarColor.RED -> MongoCar.CarColor.RED
                        DomainCar.CarColor.GREEN -> MongoCar.CarColor.GREEN
                        DomainCar.CarColor.BLUE -> MongoCar.CarColor.BLUE
                        DomainCar.CarColor.BLACK -> MongoCar.CarColor.BLACK
                        DomainCar.CarColor.WHITE -> MongoCar.CarColor.WHITE
                        DomainCar.CarColor.GREY -> MongoCar.CarColor.GREY
                        DomainCar.CarColor.YELLOW -> MongoCar.CarColor.YELLOW
                        DomainCar.CarColor.UNSPECIFIED -> MongoCar.CarColor.UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }

        @JvmStatic
        fun colorMapperDomain(): List<Arguments> {
            return MongoCar.CarColor.entries
                .map {
                    val expected: DomainCar.CarColor = when (it) {
                        MongoCar.CarColor.RED -> DomainCar.CarColor.RED
                        MongoCar.CarColor.GREEN -> DomainCar.CarColor.GREEN
                        MongoCar.CarColor.BLUE -> DomainCar.CarColor.BLUE
                        MongoCar.CarColor.BLACK -> DomainCar.CarColor.BLACK
                        MongoCar.CarColor.WHITE -> DomainCar.CarColor.WHITE
                        MongoCar.CarColor.GREY -> DomainCar.CarColor.GREY
                        MongoCar.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
                        MongoCar.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
