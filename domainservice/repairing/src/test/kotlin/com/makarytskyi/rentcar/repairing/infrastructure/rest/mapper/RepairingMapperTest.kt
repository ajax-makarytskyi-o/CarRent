package com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper

import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
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
        val repairing = randomRepairing(ObjectId().toString())
        val response = responseRepairing(repairing)

        // WHEN
        val result = repairing.toResponse()

        // THEN
        assertEquals(response, result)
    }

    @Test
    fun `update request return entity successfully`() {
        // GIVEN
        val request = updateRepairingRequest()
        val entity = repairingPatch(request)

        // WHEN
        val result = request.toPatch()

        // THEN
        assertEquals(entity, result)
    }

    @Test
    fun `update request with null fields return entity with null fields`() {
        // GIVEN
        val request = updateRepairingRequest().copy(price = null, status = null)
        val entity = repairingPatch(request)

        // WHEN
        val result = request.toPatch()

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
        val result = repairing.toResponse()

        // THEN
        assertEquals(response, result)
    }
}
