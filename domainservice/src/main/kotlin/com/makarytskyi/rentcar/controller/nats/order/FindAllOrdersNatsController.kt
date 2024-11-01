package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.controller.nats.NatsController
import com.makarytskyi.rentcar.mapper.OrderMapper.toFindAllResponse
import com.makarytskyi.rentcar.mapper.toFindAllFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class FindAllOrdersNatsController(
    private val orderService: OrderService
) : NatsController<FindAllOrdersRequest, FindAllOrdersResponse> {
    override val subject = FIND_ALL
    override val queueGroup = QUEUE_GROUP
    override val parser: Parser<FindAllOrdersRequest> = FindAllOrdersRequest.parser()
    override val defaultResponse: FindAllOrdersResponse = FindAllOrdersResponse.newBuilder()
        .also { it.failureBuilder.message = "Error happend during parsing." }.build()

    override fun handle(request: FindAllOrdersRequest): Mono<FindAllOrdersResponse> =
        orderService.findAll(request.page, request.size)
            .collectList()
            .map { it.toFindAllResponse() }
            .onErrorResume { it.toFindAllFailureResponse().toMono() }

    companion object {
        const val QUEUE_GROUP = "find_all_orders"
    }
}
