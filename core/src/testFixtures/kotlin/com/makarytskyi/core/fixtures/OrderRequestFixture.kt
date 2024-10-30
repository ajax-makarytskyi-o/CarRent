package com.makarytskyi.core.fixtures

import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.core.fixtures.Utils.getDateFromNow
import org.bson.types.ObjectId

object OrderRequestFixture {
    val tomorrow = getDateFromNow(1)
    val twoDaysAfter = getDateFromNow(2)

    fun randomCreateRequest() = CreateOrderRequestDto(
        carId = ObjectId().toString(),
        userId = ObjectId().toString(),
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun randomUpdateRequest() = UpdateOrderRequestDto(
        from = tomorrow,
        to = twoDaysAfter,
    )
}
