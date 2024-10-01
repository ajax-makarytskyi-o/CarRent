package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.dto.order.AggregatedOrderResponse
import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.service.OrderService
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

@RestController
@RequestMapping("/orders")
internal class OrderController(private val service: OrderService) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): AggregatedOrderResponse = service.getById(id)

    @GetMapping()
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): List<AggregatedOrderResponse> = service.findAll(page, size)

    @GetMapping("/car/{carId}")
    fun findByCar(@PathVariable carId: String): List<OrderResponse> = service.findByCar(carId)

    @GetMapping("/user/{userId}")
    fun findByUser(@PathVariable userId: String): List<OrderResponse> = service.findByUser(userId)

    @GetMapping("/car/{carId}/user/{userId}")
    fun findByCarAndUser(@PathVariable carId: String, @PathVariable userId: String): List<OrderResponse> =
        service.findByCarAndUser(carId, userId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody order: CreateOrderRequest): OrderResponse = service.create(order)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) = service.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody order: UpdateOrderRequest) = service.patch(id, order)
}
