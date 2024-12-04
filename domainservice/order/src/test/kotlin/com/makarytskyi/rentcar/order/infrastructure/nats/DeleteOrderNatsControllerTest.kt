package com.makarytskyi.rentcar.order.infrastructure.nats

import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.deleteOrderRequest
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.application.port.output.OrderRepositoryOutputPort
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toDeleteFailureResponse
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class DeleteOrderNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarRepositoryOutputPort

    @Autowired
    internal lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    internal lateinit var orderRepository: OrderRepositoryOutputPort

    @Test
    fun `delete should delete order and return empty success message`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()!!
        val deleteRequest = deleteOrderRequest(order.id)

        // WHEN
        val response = natsPublisher.request(DELETE, deleteRequest, DeleteOrderResponse.parser()).block()

        // THEN
        assertEquals(toDeleteFailureResponse(), response)
        orderRepository.findFullById(order.id)
            .test()
            .verifyComplete()
    }
}
