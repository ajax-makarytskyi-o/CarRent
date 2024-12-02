package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.car.infrastructure.rest.mapper.toResponse
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import kotlin.test.Test
import kotlin.test.assertEquals

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
}
