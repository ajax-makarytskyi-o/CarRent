package com.makarytskyi.gateway.infrastructure.grpc

import com.makarytskyi.gateway.application.input.OrderInputPort
import com.makarytskyi.gateway.infrastructure.mapper.OrderMapper.toGrpcProto
import com.makarytskyi.gateway.infrastructure.mapper.OrderMapper.toInternalProto
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.grpcapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdRequest
import com.makarytskyi.grpcapi.input.reqreply.order.StreamCreatedOrdersByCar.StreamCreatedOrdersByUserIdResponse
import com.makarytskyi.grpcapi.service.ReactorOrderServiceGrpc
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class OrderGrpcService(
    private val natsHandler: OrderInputPort,
) : ReactorOrderServiceGrpc.OrderServiceImplBase() {

    override fun getFullById(request: GetByIdOrderRequest): Mono<GetByIdOrderResponse> =
        request.toInternalProto().toMono()
            .flatMap { natsHandler.getFullById(it) }
            .map { it.toGrpcProto() }

    override fun create(request: CreateOrderRequest): Mono<CreateOrderResponse> =
        request.toInternalProto().toMono()
            .flatMap { natsHandler.create(it) }
            .map { it.toGrpcProto() }

    override fun streamCreatedOrdersByUserId(request: Mono<StreamCreatedOrdersByUserIdRequest>):
            Flux<StreamCreatedOrdersByUserIdResponse> =
        request.map { it.userId }
            .flatMapMany { natsHandler.streamCreatedOrdersByUserId(it) }
            .map { StreamCreatedOrdersByUserIdResponse.newBuilder().also { response -> response.order = it }.build() }
}
