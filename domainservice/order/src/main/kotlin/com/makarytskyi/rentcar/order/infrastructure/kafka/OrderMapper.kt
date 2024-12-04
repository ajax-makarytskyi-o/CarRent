package com.makarytskyi.rentcar.order.infrastructure.kafka

import com.makarytskyi.commonmodels.order.OrderCancellationNotification
import com.makarytskyi.rentcar.order.application.mapper.toProto
import com.makarytskyi.rentcar.order.domain.DomainOrder

fun DomainOrder.toNotification(): OrderCancellationNotification = OrderCancellationNotification
    .newBuilder().also {
        it.userId = this.userId
        it.order = this.toProto()
    }
    .build()
