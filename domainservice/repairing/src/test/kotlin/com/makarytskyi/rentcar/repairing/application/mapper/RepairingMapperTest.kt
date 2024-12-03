package com.makarytskyi.rentcar.repairing.application.mapper

import com.makarytskyi.commonmodels.repairing.Repairing
import com.makarytskyi.rentcar.fixtures.CarFixture.randomPrice
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomRepairing
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.domain.patch.DomainRepairingPatch
import kotlin.test.Test
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class RepairingMapperTest {

    @Test
    fun `patch mapper should return repairing with updated fields`() {
        // GIVEN
        val repairing = randomRepairing(ObjectId().toString())

        val patch = DomainRepairingPatch(
            price = randomPrice(),
            status = DomainRepairing.RepairingStatus.entries.random(),
        )

        val expected = repairing.copy(price = patch.price!!, status = patch.status!!)

        // WHEN
        val result = repairing.fromPatch(patch)

        // THEN
        assertEquals(expected, result)
    }

    @Test
    fun `patch mapper should return old repairing if patch is empty`() {
        // GIVEN
        val repairing = randomRepairing(ObjectId().toString())

        val patch = DomainRepairingPatch(
            price = null,
            status = null,
        )

        // WHEN
        val result = repairing.fromPatch(patch)

        // THEN
        assertEquals(repairing, result)
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
