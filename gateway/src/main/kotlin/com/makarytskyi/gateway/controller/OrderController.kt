package com.makarytskyi.gateway.controller

import com.makarytskyi.core.dto.order.AggregatedOrderResponseDto
import com.makarytskyi.core.dto.order.CreateOrderRequestDto
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.dto.order.UpdateOrderRequestDto
import com.makarytskyi.gateway.config.NatsClient
import com.makarytskyi.gateway.mapper.toDto
import com.makarytskyi.gateway.mapper.toProto
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_ALL
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
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
    fun getFullById(@PathVariable id: String): Mono<AggregatedOrderResponseDto> =
        natsClient.request(
            FIND_BY_ID,
            GetByIdOrderRequest.newBuilder().setId(id).build(),
            GetByIdOrderResponse.parser()
        )
            .map { it.toDto() }

    @GetMapping()
    fun findFullAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<AggregatedOrderResponseDto> =
        natsClient.request(
            FIND_ALL,
            FindAllOrdersRequest.newBuilder().setPage(page).setSize(size).build(),
            FindAllOrdersResponse.parser()
        )
            .map { it.toDto() }
            .flatMapMany { Flux.fromIterable(it) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody order: CreateOrderRequestDto): Mono<OrderResponseDto> =
        natsClient.request(
            CREATE,
            order.toProto(),
            CreateOrderResponse.parser()
        )
            .map { it.toDto() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Unit> =
        natsClient.request(
            DELETE,
            DeleteOrderRequest.newBuilder().setId(id).build(),
            DeleteOrderResponse.parser()
        )
            .thenReturn(Unit)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody patch: UpdateOrderRequestDto): Mono<OrderResponseDto> =
        natsClient.request(
            PATCH,
            PatchOrderRequest.newBuilder().setId(id).setPatch(patch.toProto()).build(),
            PatchOrderResponse.parser()
        )
            .map { it.toDto() }
}
