package com.makarytskyi.rentcar.controller.nats.order

import com.google.protobuf.Parser
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.mapper.OrderMapper.toDeleteFailureResponse
import com.makarytskyi.rentcar.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import systems.ajax.nats.handler.api.ProtoNatsMessageHandler

@Component
class DeleteOrderNatsController(
    private val orderService: OrderService,
) : ProtoNatsMessageHandler<DeleteOrderRequest, DeleteOrderResponse> {
    override val subject: String = DELETE
    override val queue: String = QUEUE_GROUP
    override val log: Logger = LoggerFactory.getLogger(DeleteOrderNatsController::class.java)
    override val parser: Parser<DeleteOrderRequest> = DeleteOrderRequest.parser()

    override fun doHandle(inMsg: DeleteOrderRequest): Mono<DeleteOrderResponse> =
        orderService.deleteById(inMsg.id)
            .thenReturn(toDeleteFailureResponse())

    override fun doOnUnexpectedError(inMsg: DeleteOrderRequest?, e: Exception): Mono<DeleteOrderResponse> =
        DeleteOrderResponse.newBuilder()
            .apply {
                failureBuilder.message = e.message
            }.build().toMono()

    companion object {
        const val QUEUE_GROUP = "delete_order"
    }
}
