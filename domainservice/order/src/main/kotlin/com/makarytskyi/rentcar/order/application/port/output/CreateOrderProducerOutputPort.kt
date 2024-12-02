package com.makarytskyi.rentcar.order.application.port.output

import com.makarytskyi.commonmodels.order.Order
import reactor.core.publisher.Mono

interface CreateOrderProducerOutputPort {
    fun sendCreateOrder(order: Order): Mono<Unit>
}
