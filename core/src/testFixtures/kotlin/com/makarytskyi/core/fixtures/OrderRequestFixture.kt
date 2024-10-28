package com.makarytskyi.core.fixtures

import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.core.fixtures.Utils.getDateFromNow
import org.bson.types.ObjectId

object OrderRequestFixture {
    val tomorrow = getDateFromNow(1)
    val twoDaysAfter = getDateFromNow(2)

    fun randomCreateRequest() = CreateOrderRequest(
        carId = ObjectId().toString(),
        userId = ObjectId().toString(),
        from = tomorrow,
        to = twoDaysAfter,
    )

    fun randomUpdateRequest() = UpdateOrderRequest(
        from = tomorrow,
        to = twoDaysAfter,
    )
}
