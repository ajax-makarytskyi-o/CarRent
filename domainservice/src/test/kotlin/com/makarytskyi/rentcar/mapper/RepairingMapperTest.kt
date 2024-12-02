package com.makarytskyi.rentcar.mapper

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.emptyProtoRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.repairingPatch
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseAggregatedRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.responseRepairing
import com.makarytskyi.rentcar.fixtures.RepairingFixture.updateRepairingRequest
import com.makarytskyi.rentcar.repairing.application.mapper.toProto
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toPatch
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toResponse
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

    @ParameterizedTest
    @MethodSource("statusMappingTestParameters")
    fun `status mapper should return proto status`(
        status: DomainRepairing.RepairingStatus,
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
            return DomainRepairing.RepairingStatus.entries
                .map {
                    val expected: Repairing.RepairingStatus = when (it) {
                        DomainRepairing.RepairingStatus.PENDING ->
                            Repairing.RepairingStatus.REPAIRING_STATUS_PENDING

                        DomainRepairing.RepairingStatus.IN_PROGRESS ->
                            Repairing.RepairingStatus.REPAIRING_STATUS_IN_PROGRESS

                        DomainRepairing.RepairingStatus.COMPLETED ->
                            Repairing.RepairingStatus.REPAIRING_STATUS_COMPLETED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
