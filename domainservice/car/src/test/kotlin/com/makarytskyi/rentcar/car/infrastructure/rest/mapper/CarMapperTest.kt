package com.makarytskyi.rentcar.car.infrastructure.rest.mapper

import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CarResponse
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.CreateCarRequest
import com.makarytskyi.rentcar.car.infrastructure.rest.dto.UpdateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarDtoRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarDtoRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `create request mapper should return domain car`() {
        // GIVEN
        val createCarRequest = createCarDtoRequest()
        val domainCarRequest = createCarRequest(createCarRequest)

        // WHEN
        val result = createCarRequest.toDomain()

        // THEN
        assertEquals(domainCarRequest, result)
    }

    @Test
    fun `update request mapper should return patch car`() {
        // GIVEN
        val updateCarRequest = updateCarDtoRequest()
        val patch = updateCarRequest(updateCarRequest)

        // WHEN
        val result = updateCarRequest.toPatch()

        // THEN
        assertEquals(patch, result)
    }

    @ParameterizedTest
    @MethodSource("createRequestColor")
    fun `create request mapper should return corresponding color`(
        requestColor: CreateCarRequest.CarColor,
        domainColor: DomainCar.CarColor,
    ) {
        // WHEN
        val result = requestColor.toDomain()

        // THEN
        assertEquals(domainColor, result)
    }

    @ParameterizedTest
    @MethodSource("updateRequestColor")
    fun `update request mapper should return corresponding color`(
        requestColor: UpdateCarRequest.CarColor,
        domainColor: DomainCar.CarColor,
    ) {
        // WHEN
        val result = requestColor.toDomain()

        // THEN
        assertEquals(domainColor, result)
    }

    @ParameterizedTest
    @MethodSource("responseCarColor")
    fun `update request mapper should return corresponding color`(
        domainColor: DomainCar.CarColor,
        responseColor: CarResponse.CarColor,
    ) {
        // WHEN
        val result = domainColor.toResponse()

        // THEN
        assertEquals(responseColor, result)
    }

    companion object {
        @JvmStatic
        fun createRequestColor(): List<Arguments> {
            return CreateCarRequest.CarColor.entries
                .map {
                    val expected: DomainCar.CarColor = when (it) {
                        CreateCarRequest.CarColor.RED -> DomainCar.CarColor.RED
                        CreateCarRequest.CarColor.GREEN -> DomainCar.CarColor.GREEN
                        CreateCarRequest.CarColor.BLUE -> DomainCar.CarColor.BLUE
                        CreateCarRequest.CarColor.BLACK -> DomainCar.CarColor.BLACK
                        CreateCarRequest.CarColor.WHITE -> DomainCar.CarColor.WHITE
                        CreateCarRequest.CarColor.GREY -> DomainCar.CarColor.GREY
                        CreateCarRequest.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
                        CreateCarRequest.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }

        @JvmStatic
        fun updateRequestColor(): List<Arguments> {
            return UpdateCarRequest.CarColor.entries
                .map {
                    val expected: DomainCar.CarColor = when (it) {
                        UpdateCarRequest.CarColor.RED -> DomainCar.CarColor.RED
                        UpdateCarRequest.CarColor.GREEN -> DomainCar.CarColor.GREEN
                        UpdateCarRequest.CarColor.BLUE -> DomainCar.CarColor.BLUE
                        UpdateCarRequest.CarColor.BLACK -> DomainCar.CarColor.BLACK
                        UpdateCarRequest.CarColor.WHITE -> DomainCar.CarColor.WHITE
                        UpdateCarRequest.CarColor.GREY -> DomainCar.CarColor.GREY
                        UpdateCarRequest.CarColor.YELLOW -> DomainCar.CarColor.YELLOW
                        UpdateCarRequest.CarColor.UNSPECIFIED -> DomainCar.CarColor.UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }

        @JvmStatic
        fun responseCarColor(): List<Arguments> {
            return DomainCar.CarColor.entries
                .map {
                    val expected: CarResponse.CarColor = when (it) {
                        DomainCar.CarColor.RED -> CarResponse.CarColor.RED
                        DomainCar.CarColor.GREEN -> CarResponse.CarColor.GREEN
                        DomainCar.CarColor.BLUE -> CarResponse.CarColor.BLUE
                        DomainCar.CarColor.BLACK -> CarResponse.CarColor.BLACK
                        DomainCar.CarColor.WHITE -> CarResponse.CarColor.WHITE
                        DomainCar.CarColor.GREY -> CarResponse.CarColor.GREY
                        DomainCar.CarColor.YELLOW -> CarResponse.CarColor.YELLOW
                        DomainCar.CarColor.UNSPECIFIED -> CarResponse.CarColor.UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
