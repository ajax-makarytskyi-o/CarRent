package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toGetByIdResponse
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetByIdOrderNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<GetByIdOrderRequest, GetByIdOrderResponse> {
    override val subject = FIND_BY_ID
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<GetByIdOrderRequest> = GetByIdOrderRequest.parser()
    override val defaultResponse: GetByIdOrderResponse = GetByIdOrderResponse.getDefaultInstance()

    override fun handle(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        orderService.getById(request.id)
            .map { it.toGetByIdResponse() }
            .onErrorResume { it.toGetByIdResponse() }

    companion object {
        const val QUEUE_GROUP = "get_by_id_order"
    }
}
