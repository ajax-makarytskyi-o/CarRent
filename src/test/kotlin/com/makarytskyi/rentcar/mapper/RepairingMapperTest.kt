package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import fixtures.CarFixture.randomCar
import fixtures.RepairingFixture.createRepairingEntity
import fixtures.RepairingFixture.createRepairingRequest
import fixtures.RepairingFixture.emptyAggregatedRepairing
import fixtures.RepairingFixture.emptyAggregatedRepairingResponse
import fixtures.RepairingFixture.emptyRepairing
import fixtures.RepairingFixture.emptyRepairingResponse
import fixtures.RepairingFixture.randomAggregatedRepairing
import fixtures.RepairingFixture.randomRepairing
import fixtures.RepairingFixture.responseAggregatedRepairing
import fixtures.RepairingFixture.responseRepairing
import fixtures.RepairingFixture.updateRepairingEntity
import fixtures.RepairingFixture.updateRepairingRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId

class RepairingMapperTest {
    @Test
    fun `repairing mapper should return response successfully`() {
        // GIVEN
        val repairing = randomRepairing(ObjectId())
        val response = responseRepairing(repairing)

        // WHEN
        val result = RepairingResponse.from(repairing)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `repairing mapper return response with default fields if car fields are null`() {
        // GIVEN
        val repairing = emptyRepairing()
        val response = emptyRepairingResponse()

        // WHEN
        val result = RepairingResponse.from(repairing)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `create request return entity successfully`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car)
        val entity = createRepairingEntity(request)

        // WHEN
        val result = CreateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `create request with null fields return entity with null fields`() {
        // GIVEN
        val car = randomCar()
        val request = createRepairingRequest(car).copy(
            date = null,
            price = null,
            status = null
        )
        val entity = createRepairingEntity(request)

        // WHEN
        val result = CreateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateRepairingRequest()
        val entity = updateRepairingEntity(request)

        // WHEN
        val result = UpdateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = updateRepairingRequest().copy(price = null, status = null)
        val entity = updateRepairingEntity(request)

        // WHEN
        val result = UpdateRepairingRequest.toEntity(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `aggregated repairing mapper should return response successfully`() {
        // GIVEN
        val car = randomCar()
        val repairing = randomAggregatedRepairing(car)
        val response = responseAggregatedRepairing(repairing)

        // WHEN
        val result = AggregatedRepairingResponse.from(repairing)

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `aggregated repairing mapper return response with default fields if car fields are null`() {
        // GIVEN
        val repairing = emptyAggregatedRepairing()
        val response = emptyAggregatedRepairingResponse()

        // WHEN
        val result = AggregatedRepairingResponse.from(repairing)

        // THEN
        assertEquals(response, result)
    }
}
