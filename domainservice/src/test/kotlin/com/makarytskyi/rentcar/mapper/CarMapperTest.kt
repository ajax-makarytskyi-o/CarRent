package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.car.CreateCarRequest
import com.makarytskyi.rentcar.dto.car.UpdateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.carPatch
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarEntity
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.dtoColor
import com.makarytskyi.rentcar.fixtures.CarFixture.emptyResponseCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.responseCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import com.makarytskyi.rentcar.model.MongoCar
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

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
    fun `response mapper throws IllegalArgumentException if car id is null`() {
        // GIVEN
        val emptyCar = MongoCar()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { CarResponse.from(emptyCar) }
    }

    @Test
    fun `response mapper return empty response if fields except id are null`() {
        // GIVEN
        val emptyCar = MongoCar().copy(id = ObjectId())
        val response = CarResponse.from(emptyCar)

        // WHEN // THEN
        assertEquals(response, emptyResponseCar().copy(id = emptyCar.id.toString()))
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

    @ParameterizedTest
    @EnumSource(MongoCar.CarColor::class)
    fun `color mapper should return response color`(color: MongoCar.CarColor) {
        // GIVEN
        val expected = dtoColor(color)

        // WHEN
        val result = color.toResponse()

        // THEN
        assertEquals(expected, result)
    }
}
