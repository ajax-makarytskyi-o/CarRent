package com.makarytskyi.rentcar.mapper

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingEntity
import com.makarytskyi.rentcar.fixtures.RepairingFixture.createRepairingRequest
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyProtoRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.repairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateRepairingRequest
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.model.MongoRepairing.RepairingStatus
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

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
    fun `repairing mapper throws IllegalArgumentException if id is null`() {
        // GIVEN
        val repairing = emptyRepairing()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { RepairingResponse.from(repairing) }
    }

    @Test
    fun `repairing mapper throws IllegalArgumentException if car id is null`() {
        // GIVEN
        val repairing = emptyRepairing().copy(id = ObjectId())

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { RepairingResponse.from(repairing) }
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
    fun `aggregated repairing mapper throws IllegalArgumentException if car id is null`() {
        // GIVEN
        val repairing = emptyAggregatedRepairing()

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { AggregatedRepairingResponse.from(repairing) }
    }

    @Test
    fun `repairing mapper should return proto with default fields if repairing has empty fields`() {
        // GIVEN
        val repairing = emptyRepairing()
        val expected = emptyProtoRepairing()

        // WHEN
        val result = repairing.toProto()

        // THEN
        assertEquals(expected, result)
    }

    @ParameterizedTest
    @MethodSource("statusMappingTestParameters")
    fun `status mapper should return proto status`(
        status: RepairingStatus,
        expectedStatus: Repairing.RepairingStatus,
    ) {
        // WHEN
        val result = status.toProto()

        // THEN
        assertEquals(expectedStatus, result)
    }

    companion object {

        @JvmStatic
        fun statusMappingTestParameters(): List<Arguments> {
            return MongoRepairing.RepairingStatus.entries
                .map {
                    val expected: Repairing.RepairingStatus = when (it) {
                        RepairingStatus.PENDING -> Repairing.RepairingStatus.REPAIRING_STATUS_PENDING
                        RepairingStatus.IN_PROGRESS -> Repairing.RepairingStatus.REPAIRING_STATUS_IN_PROGRESS
                        RepairingStatus.COMPLETED -> Repairing.RepairingStatus.REPAIRING_STATUS_COMPLETED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
