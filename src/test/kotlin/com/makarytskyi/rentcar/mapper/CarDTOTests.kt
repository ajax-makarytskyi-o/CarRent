package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.model.MongoCar
import fixtures.CarFixture.createCarRequest
import fixtures.CarFixture.existingCar
import fixtures.CarFixture.updateCarRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class CarDTOTests {
    @Test
    fun `response mapper should return response successfully`() {
        // GIVEN
        val car = existingCar()
        val response = CarResponse(
            id = car.id.toString(),
            brand = car.brand!!,
            model = car.model!!,
            price = car.price!!,
            year = car.year,
            plate = car.plate!!,
            color = car.color,
        )

        // WHEN
        val result = CarResponse.from(car)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `response mapper return response with default fields if car fields are null`() {
        // GIVEN
        val emptyCar = MongoCar(
            id = null,
            brand = null,
            model = null,
            price = null,
            year = null,
            plate = null,
            color = null,
        )

        val response = CarResponse(
            id = "",
            brand = "",
            model = "",
            price = 0,
            year = null,
            plate = "",
            color = null,
        )

        // WHEN
        val result = CarResponse.from(emptyCar)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `create request mapper should return entity successfully`() {
        // GIVEN
        val request = createCarRequest()
        val entity = MongoCar(
            id = null,
            brand = request.brand,
            model = request.model,
            price = request.price,
            year = request.year,
            plate = request.plate,
            color = request.color,
        )

        // WHEN
        val result = CreateCarRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `create request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = createCarRequest().copy(
            year = null,
            color = null,
        )

        val entity = MongoCar(
            id = null,
            brand = request.brand,
            model = request.model,
            price = request.price,
            year = null,
            plate = request.plate,
            color = null,
        )

        // WHEN
        val result = CreateCarRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `update request mapper should return entity successfully`() {
        // GIVEN
        val request = updateCarRequest()
        val entity = MongoCar(
            id = null,
            brand = null,
            model = null,
            price = request.price,
            year = null,
            plate = null,
            color = request.color,
        )

        // WHEN
        val result = UpdateCarRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }

    @Test
    fun `update request return entity with null fields if request fields are null`() {
        // GIVEN
        val request = updateCarRequest().copy(
            price = null,
            color = null,
        )

        val entity = MongoCar(
            id = null,
            brand = null,
            model = null,
            price = null,
            year = null,
            plate = null,
            color = null,
        )

        // WHEN
        val result = UpdateCarRequest.toEntity(request)

        // THEN
        assertEquals(result, entity)
    }
}
