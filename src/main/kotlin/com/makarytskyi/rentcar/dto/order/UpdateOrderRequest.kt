package com.makarytskyi.rentcar.dto.order

import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import java.util.Date

data class UpdateOrderRequest(
    val from: Date?,
    val to: Date?,
) {

    companion object {
        fun toPatch(orderRequest: UpdateOrderRequest) = MongoOrderPatch(
            orderRequest.from,
            orderRequest.to,
        )
    }
}
