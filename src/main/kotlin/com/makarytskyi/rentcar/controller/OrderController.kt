package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.dto.order.CreateOrderRequest
import com.makarytskyi.rentcar.dto.order.OrderResponse
import com.makarytskyi.rentcar.dto.order.UpdateOrderRequest
import com.makarytskyi.rentcar.service.OrderService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/order")
class OrderController(private val service: OrderService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): OrderResponse = service.getById(id)

    @GetMapping("/all")
    fun findAll(): List<OrderResponse> = service.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody order: CreateOrderRequest): OrderResponse = service.create(order)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String) = service.deleteById(id)

    @GetMapping("/user/{userId}")
    fun findByUserId(@PathVariable userId: String): List<OrderResponse> = service.findByUserId(userId)

    @GetMapping("/car/{carId}")
    fun findByCarId(@PathVariable carId: String): List<OrderResponse> = service.findByCarId(carId)

    @PutMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody order: UpdateOrderRequest) = service.update(id, order)
}
