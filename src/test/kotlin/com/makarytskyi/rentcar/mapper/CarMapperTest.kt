package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.MongoCar
import fixtures.CarFixture.carPatch
import fixtures.CarFixture.createCarEntity
import fixtures.CarFixture.createCarRequest
import fixtures.CarFixture.emptyResponseCar
import fixtures.CarFixture.randomCar
import fixtures.CarFixture.responseCar
import fixtures.CarFixture.updateCarRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class CarMapperTest {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val response = responseCar(car)

        // WHEN
        val result = CarResponse.from(car)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `response mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyCar = MongoCar()
        val response = emptyResponseCar()

        // WHEN
        val result = CarResponse.from(emptyCar)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create request mapper should return entity successfully`() {
        // GIVEN
        val request = createCarRequest()
        val entity = createCarEntity(request)

        // WHEN
        val result = CreateCarRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `create request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = createCarRequest().copy(
            year = null,
            color = null,
        )

        val entity = createCarEntity(request)

        // WHEN
        val result = CreateCarRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request mapper should return entity successfully`() {
        // GIVEN
        val request = updateCarRequest()
        val entity = carPatch(request)

        // WHEN
        val result = UpdateCarRequest.toPatch(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = updateCarRequest().copy(
            price = null,
            color = null,
        )

        val entity = carPatch(request)

        // WHEN
        val result = UpdateCarRequest.toPatch(request)

        // THEN
        assertEquals(entity, result)
    }
}
