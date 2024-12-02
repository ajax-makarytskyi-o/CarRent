package com.makarytskyi.rentcar.fixtures

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.rentcar.order.application.mapper.toProto
import com.makarytskyi.rentcar.order.domain.DomainOrder

object NotificationFixture {
    fun notification(order: DomainOrder): OrderCancellationNotification =
        OrderCancellationNotification
            .newBuilder().also {
                it.userId = order.userId
                it.order = order.toProto()
            }
            .build()
}
