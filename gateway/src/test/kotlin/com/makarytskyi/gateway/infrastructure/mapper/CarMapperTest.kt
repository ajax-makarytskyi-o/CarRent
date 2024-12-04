package com.makarytskyi.gateway.infrastructure.mapper

import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.gateway.fixtures.CarProtoFixture.dtoColor
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class CarMapperTest {

    @ParameterizedTest
    @EnumSource(CarColor::class)
    fun `car mapper should map proto color to dto color`(protoColor: CarColor) {
        // GIVEN
        val expected = dtoColor(protoColor)

        // WHEN
        val result = protoColor.toDto()

        // THEN
        assertEquals(expected, result)
    }
}
