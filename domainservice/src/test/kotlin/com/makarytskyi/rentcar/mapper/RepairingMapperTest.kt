package com.makarytskyi.rentcar.mapper

import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingEntity
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyAggregatedRepairingResponse
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairingResponse
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.repairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateRepairingRequest
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
        val entity = repairingPatch(request)

        // WHEN
        val result = UpdateRepairingRequest.toPatch(request)

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = updateRepairingRequest().copy(price = null, status = null)
        val entity = repairingPatch(request)

        // WHEN
        val result = UpdateRepairingRequest.toPatch(request)

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
