package com.makarytskyi.gateway.controller

import com.makarytskyi.core.dto.order.AggregatedOrderResponse
import com.makarytskyi.core.dto.order.CreateOrderRequest
import com.makarytskyi.core.dto.order.OrderResponse
import com.makarytskyi.core.dto.order.UpdateOrderRequest
import com.makarytskyi.gateway.config.NatsClient
import com.makarytskyi.gateway.mapper.toDto
import com.makarytskyi.gateway.mapper.toProto
import com.makarytskyi.internalapi.reqreply.create.CreateOrderProtoResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_all.FindAllOrdersProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.find_by_id.GetByIdOrderProtoResponse
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.patch.PatchOrderProtoResponse
import io.nats.client.Connection
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(private val natsClient: NatsClient) {

    @GetMapping("/{id}")
    fun getFullById(@PathVariable id: String): Mono<AggregatedOrderResponse> =
        natsClient.request(
            FIND_BY_ID,
            GetByIdOrderProtoRequest.newBuilder().setId(id).build(),
            GetByIdOrderProtoResponse.parser()
        )
            .map { it.toDto() }

    @GetMapping()
    fun findFullAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<AggregatedOrderResponse> =
        natsClient.request(
            FIND_ALL,
            FindAllOrdersProtoRequest.newBuilder().setPage(page).setSize(size).build(),
            FindAllOrdersProtoResponse.parser()
        )
            .map { it.toDto() }
            .flatMapMany { Flux.fromIterable(it) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody order: CreateOrderRequest): Mono<OrderResponse> =
        natsClient.request(
            CREATE,
            order.toProto(),
            CreateOrderProtoResponse.parser()
        )
            .map { it.toDto() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> =
        natsClient.request(
            DELETE,
            DeleteOrderProtoRequest.newBuilder().setId(id).build(),
            DeleteOrderProtoResponse.parser()
        )
            .map { Unit }

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody patch: UpdateOrderRequest): Mono<OrderResponse> =
        natsClient.request(
            PATCH,
            PatchOrderProtoRequest.newBuilder().setId(id).setPatch(patch.toProto()).build(),
            PatchOrderProtoResponse.parser()
        )
            .map { it.toDto() }
}
