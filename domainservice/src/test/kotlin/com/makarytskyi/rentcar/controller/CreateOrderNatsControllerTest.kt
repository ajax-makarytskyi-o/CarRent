package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.createOrderRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failureCreateResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successfulCreateResponse
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.UserRepository
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class CreateOrderNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!

        val protoRequest = createOrderRequest(car, user)
        val orderResponse = successfulCreateResponse(
            protoRequest,
            car.price!!.toDouble()
        ).toBuilder().successBuilder.orderBuilder.clearId().build()

        // WHEN
        val response = natsPublisher.request(CREATE, protoRequest, CreateOrderResponse.parser()).block()!!

        // THEN
        assertNotNull(response.success.order.id)
        assertEquals(orderResponse, response.toBuilder().successBuilder.orderBuilder.clearId().build())
    }

    @Test
    fun `create should return error message if user doesn't exist`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = randomUser().copy(id = null)
        val protoRequest = createOrderRequest(car, user)
        val exception = NotFoundException("User with id null is not found")
        val protoResponse = failureCreateResponse(exception)

        // WHEN
        val response = natsPublisher.request(CREATE, protoRequest, CreateOrderResponse.parser()).block()

        // THEN
        assertEquals(protoResponse, response)
    }
}
