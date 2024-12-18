package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.rentcar.mapper.OrderMapper.toProto

object NotificationFixture {
    fun notification(order: OrderResponseDto): OrderCancellationNotification =
        OrderCancellationNotification
            .newBuilder().also {
                it.userId = order.userId
                it.order = order.toProto()
            }
            .build()
}
