package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.rentcar.mapper.OrderMapper.toFindAllResponse
import com.makarytskyi.rentcar.mapper.toFindAllFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class FindAllOrdersNatsController(
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<FindAllOrdersRequest, FindAllOrdersResponse> {
    override val subject: String = FIND_ALL
    override val queue: String = QUEUE_GROUP
    override val log: Logger = LoggerFactory.getLogger(FindAllOrdersNatsController::class.java)
    override val parser: Parser<FindAllOrdersRequest> = FindAllOrdersRequest.parser()

    override fun doHandle(inMsg: FindAllOrdersRequest): Mono<FindAllOrdersResponse> =
        orderService.findAll(inMsg.page, inMsg.size)
            .collectList()
            .map { it.toFindAllResponse() }
            .onErrorResume { it.toFindAllFailureResponse().toMono() }

    override fun doOnUnexpectedError(inMsg: FindAllOrdersRequest?, e: Exception): Mono<FindAllOrdersResponse> =
        e.toFindAllFailureResponse().toMono()

    companion object {
        const val QUEUE_GROUP = "find_all_orders"
    }
}
