package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.dto.car.CarResponseDto
import com.makarytskyi.internalapi.commonmodels.car.CarColor
import java.util.stream.Stream
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CarMapperTest {

    @ParameterizedTest
    @MethodSource("enumProvider")
    fun `car mapper should map proto color to dto color`(protoColor: CarColor, dtoColor: CarResponseDto.CarColor) {
        // WHEN
        val result = protoColor.toDto()

        // THEN
        assertEquals(dtoColor, result)
    }

    companion object {
        @JvmStatic
        fun enumProvider() = Stream.of(
            Arguments.of(CarColor.CAR_COLOR_BLUE, CarResponseDto.CarColor.BLUE),
            Arguments.of(CarColor.CAR_COLOR_WHITE, CarResponseDto.CarColor.WHITE),
            Arguments.of(CarColor.CAR_COLOR_RED, CarResponseDto.CarColor.RED),
            Arguments.of(CarColor.CAR_COLOR_GREY, CarResponseDto.CarColor.GREY),
            Arguments.of(CarColor.CAR_COLOR_GREEN, CarResponseDto.CarColor.GREEN),
            Arguments.of(CarColor.CAR_COLOR_YELLOW, CarResponseDto.CarColor.YELLOW),
            Arguments.of(CarColor.CAR_COLOR_BLACK, CarResponseDto.CarColor.BLACK),
            Arguments.of(CarColor.CAR_COLOR_UNSPECIFIED, CarResponseDto.CarColor.UNSPECIFIED),
            Arguments.of(CarColor.UNRECOGNIZED, CarResponseDto.CarColor.UNSPECIFIED)
        )
    }
}
