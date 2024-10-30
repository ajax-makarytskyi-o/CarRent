package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.toFindAllResponse
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.service.OrderService
import io.nats.client.Connection
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FindAllOrdersNatsController(
    override val connection: Connection,
    private val orderService: OrderService
) : NatsController<FindAllOrdersRequest, FindAllOrdersResponse> {
    override val subject = FIND_ALL
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<FindAllOrdersRequest> = FindAllOrdersRequest.parser()
    override val defaultResponse: FindAllOrdersResponse = FindAllOrdersResponse.getDefaultInstance()

    override fun handle(request: FindAllOrdersRequest): Mono<FindAllOrdersResponse> =
        orderService.findAll(request.page, request.size)
            .map { it.toProto() }
            .collectList()
            .map { it.toFindAllResponse() }
            .onErrorResume { it.toFindAllResponse() }

    companion object {
        const val QUEUE_GROUP = "find_all_orders"
    }
}
