package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.internalapi.commonmodels.order.OrderCancellationUserNotification
import com.makarytskyi.rentcar.mapper.OrderMapper.toProto
import com.makarytskyi.rentcar.mapper.OrderMapper.toResponse
import com.makarytskyi.rentcar.model.MongoOrder
import java.math.BigDecimal

object NotificationFixture {
    fun notification(order: MongoOrder, price: BigDecimal): OrderCancellationUserNotification =
        OrderCancellationUserNotification.newBuilder()
            .also {
                it.setUserId(order.userId.toString())
                it.setOrder(order.toResponse(price).toProto())
            }
            .build()
}
