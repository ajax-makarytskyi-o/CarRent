package com.makarytskyi.gateway.infrastructure.nats

import com.makarytskyi.commonmodels.order.Order
import com.makarytskyi.gateway.application.input.OrderInputPort
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.NatsHandlerManager
import systems.ajax.nats.publisher.api.NatsMessagePublisher

@Component
class OrderNatsAdapter(
    private val natsPublisher: NatsMessagePublisher,
    private val manager: NatsHandlerManager,
) : OrderInputPort {
    override fun getFullById(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        natsPublisher.request(
            GET_BY_ID,
            request,
            GetByIdOrderResponse.parser()
        )

    override fun create(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        natsPublisher.request(
            CREATE,
            request,
            CreateOrderResponse.parser()
        )

    override fun streamCreatedOrdersByUserId(userId: String): Flux<Order> =
        userId.toMono().flatMapMany {
            manager.subscribe(NatsSubject.Order.createOrderOnCar(it)) { message ->
                Order.parser().parseFrom(message.data)
            }
        }
}
