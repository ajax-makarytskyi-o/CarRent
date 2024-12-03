package com.makarytskyi.rentcar.order.infrastructure.nats.mapper

import com.makarytskyi.commonmodels.car.Car.CarColor
import com.makarytskyi.rentcar.car.domain.DomainCar
import kotlin.test.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

class OrderProtoMapperTest {
    @ParameterizedTest
    @MethodSource("colorMapperProto")
    fun `proto mapper should return corresponding color`(
        domainColor: DomainCar.CarColor,
        protoColor: CarColor,
    ) {
        // WHEN
        val result = domainColor.toProto()

        // THEN
        assertEquals(protoColor, result)
    }

    companion object {
        @JvmStatic
        fun colorMapperProto(): List<Arguments> {
            return DomainCar.CarColor.entries
                .map {
                    val expected: CarColor = when (it) {
                        DomainCar.CarColor.RED -> CarColor.CAR_COLOR_RED
                        DomainCar.CarColor.GREEN -> CarColor.CAR_COLOR_GREEN
                        DomainCar.CarColor.BLUE -> CarColor.CAR_COLOR_BLUE
                        DomainCar.CarColor.BLACK -> CarColor.CAR_COLOR_BLACK
                        DomainCar.CarColor.WHITE -> CarColor.CAR_COLOR_WHITE
                        DomainCar.CarColor.GREY -> CarColor.CAR_COLOR_GREY
                        DomainCar.CarColor.YELLOW -> CarColor.CAR_COLOR_YELLOW
                        DomainCar.CarColor.UNSPECIFIED -> CarColor.CAR_COLOR_UNSPECIFIED
                    }
                    it to expected
                }
                .map { (actual, expected) ->
                    arguments(actual, expected)
                }
        }
    }
}
