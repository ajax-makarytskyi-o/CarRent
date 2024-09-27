package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.car.CarResponse
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.aggregated.AggregatedMongoRepairing
import fixtures.CarFixture.randomCar
import fixtures.RepairingFixture.createRepairingRequest
import fixtures.RepairingFixture.randomAggregatedRepairing
import fixtures.RepairingFixture.randomRepairing
import fixtures.RepairingFixture.updateRepairingRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId


class RepairingDTOTests {
    @Test
    fun `repairing mapper should return response successfully`() {
        // GIVEN
        val carId = ObjectId()
        val repairing = randomRepairing(carId)

        val response = RepairingResponse(
            repairing.id?.toString().orEmpty(),
            repairing.carId?.toString().orEmpty(),
            repairing.date,
            repairing.price,
            repairing.status,
        )

        // WHEN
        val result = RepairingResponse.from(repairing)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `repairing mapper return response with default fields if car fields are null`() {
        // GIVEN
        val repairing = MongoRepairing(
            id = null,
            carId = null,
            date = null,
            price = null,
            status = null,
        )

        val response = RepairingResponse(
            "",
            "",
            null,
            null,
            null,
        )

        // WHEN
        val result = RepairingResponse.from(repairing)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `create request return entity successfully`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        val response = MongoRepairing(
            carId = ObjectId(request.carId),
            date = request.date,
            price = request.price,
            status = request.status,
        )

        // WHEN
        val result = CreateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `create request with null fields return entity with null fields`() {
        // GIVEN
        val car = randomCar()
        val request = CreateRepairingRequest(
            carId = car.id.toString(),
            date = null,
            price = null,
            status = null,
        )

        val response = MongoRepairing(
            carId = ObjectId(request.carId),
            date = null,
            price = null,
            status = null,
        )

        // WHEN
        val result = CreateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateRepairingRequest()
        val response = MongoRepairing(
            carId = null,
            date = null,
            price = request.price,
            status = request.status,
        )

        // WHEN
        val result = UpdateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = UpdateRepairingRequest(
            status = null,
            price = null
        )

        val response = MongoRepairing(
            carId = null,
            date = null,
            price = null,
            status = null,
        )

        // WHEN
        val result = UpdateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `aggregated repairing mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)

        val response = AggregatedRepairingResponse(
            id = repairing.id.toString(),
            car = CarResponse.from(car),
            date = repairing.date,
            price = repairing.price,
            status = repairing.status
        )

        // WHEN
        val result = AggregatedRepairingResponse.from(repairing)

        // THEN
        assertEquals(result, response)
    }

    @Test
    fun `aggregated repairing mapper return response with default fields if car fields are null`() {
        // GIVEN
        val repairing = AggregatedMongoRepairing(
            id = null,
            car = null,
            date = null,
            price = null,
            status = null
        )

        val response = AggregatedRepairingResponse(
            id = "",
            car = null,
            date = null,
            price = null,
            status = null
        )

        // WHEN
        val result = AggregatedRepairingResponse.from(repairing)

        // THEN
        assertEquals(result, response)
    }
}

