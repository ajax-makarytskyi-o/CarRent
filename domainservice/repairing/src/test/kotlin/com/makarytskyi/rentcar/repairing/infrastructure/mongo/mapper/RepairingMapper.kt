package com.makarytskyi.rentcar.repairing.infrastructure.mongo.mapper

import com.makarytskyi.rentcar.fixtures.CarFixture.randomMongoCar
import com.makarytskyi.rentcar.fixtures.RepairingFixture.randomAggregatedMongoRepairing
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.mongo.entity.MongoRepairing
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class RepairingMapper {
    @Test
    fun `domain mapper should throw IllegalArgumentException if car is null`() {
        // GIVEN
        val repairing = randomAggregatedMongoRepairing(null)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { repairing.toDomain() }
    }

    @Test
    fun `domain mapper should throw IllegalArgumentException if date is null`() {
        // GIVEN
        val repairing = randomAggregatedMongoRepairing(randomMongoCar()).copy(date = null)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { repairing.toDomain() }
    }

    @Test
    fun `domain mapper should throw IllegalArgumentException if price is null`() {
        // GIVEN
        val repairing = randomAggregatedMongoRepairing(randomMongoCar()).copy(price = null)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { repairing.toDomain() }
    }

    @Test
    fun `domain mapper should return repairing with default status if status is null`() {
        // GIVEN
        val repairing = randomAggregatedMongoRepairing(randomMongoCar()).copy(status = null)

        // WHEN
        val result = repairing.toDomain()

        // THEN
        assertEquals(DomainRepairing.RepairingStatus.PENDING, result.status)
    }

    @ParameterizedTest
    @MethodSource("domainStatusMapping")
    fun `status mapper should return domain status`(
        mongoStatus: MongoRepairing.RepairingStatus,
        domainStatus: DomainRepairing.RepairingStatus,
    ) {
        // WHEN
        val result = mongoStatus.toDomain()

        // THEN
        assertEquals(domainStatus, result)
    }

    companion object {
        @JvmStatic
        fun domainStatusMapping(): List<Arguments> {
            return MongoRepairing.RepairingStatus.entries
                .map {
                    val expected: DomainRepairing.RepairingStatus = when (it) {
                        MongoRepairing.RepairingStatus.PENDING ->
                            DomainRepairing.RepairingStatus.PENDING

                        MongoRepairing.RepairingStatus.IN_PROGRESS ->
                            DomainRepairing.RepairingStatus.IN_PROGRESS

                        MongoRepairing.RepairingStatus.COMPLETED ->
                            DomainRepairing.RepairingStatus.COMPLETED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
