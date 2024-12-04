package com.makarytskyi.rentcar.order.infrastructure.nats

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.CREATE
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.createOrderRequestProto
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failureCreateResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successfulCreateResponse
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class CreateOrderNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarRepositoryOutputPort

    @Autowired
    internal lateinit var userRepository: UserRepositoryOutputPort

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!

        val protoRequest = createOrderRequestProto(car, user)
        val orderResponse = successfulCreateResponse(
            protoRequest,
            car.price.toDouble()
        ).toBuilder().successBuilder.orderBuilder.clearId().build()

        // WHEN
        val response = natsPublisher.request(CREATE, protoRequest, CreateOrderResponse.parser()).block()!!

        // THEN
        assertNotNull(response.success.order.id, "Response should contain order with non-null id")
        assertEquals(orderResponse, response.toBuilder().successBuilder.orderBuilder.clearId().build())
    }

    @Test
    fun `create should return error message if user doesn't exist`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = randomUser()
        val protoRequest = createOrderRequestProto(car, user)
        val exception = NotFoundException("User with id ${user.id} is not found")
        val protoResponse = failureCreateResponse(exception)

        // WHEN
        val response = natsPublisher.request(CREATE, protoRequest, CreateOrderResponse.parser()).block()

        // THEN
        assertEquals(protoResponse, response)
    }
}
