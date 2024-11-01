package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.GET_BY_ID
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.OrderMapper.toGetByIdResponse
import com.makarytskyi.rentcar.mapper.toGetByIdFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetByIdOrderNatsController(
    private val orderService: OrderService
) : NatsController<GetByIdOrderRequest, GetByIdOrderResponse> {
    override val subject = GET_BY_ID
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<GetByIdOrderRequest> = GetByIdOrderRequest.parser()
    override val defaultResponse: GetByIdOrderResponse = GetByIdOrderResponse.newBuilder()
        .also { it.failureBuilder.message = "Error happend during parsing." }.build()

    override fun handle(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        orderService.getById(request.id)
            .map { it.toGetByIdResponse() }
            .onErrorResume { it.toGetByIdFailureResponse().toMono() }

    companion object {
        const val QUEUE_GROUP = "get_by_id_order"
    }
}
