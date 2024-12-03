package com.makarytskyi.rentcar.order.infrastructure.nats

import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.deleteOrderRequest
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.application.port.output.OrderMongoOutputPort
import com.makarytskyi.rentcar.order.infrastructure.nats.mapper.OrderProtoMapper.toDeleteFailureResponse
import com.makarytskyi.rentcar.user.application.port.output.UserOutputPort
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class DeleteOrderNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarOutputPort

    @Autowired
    internal lateinit var userRepository: UserOutputPort

    @Autowired
    internal lateinit var orderRepository: OrderMongoOutputPort

    @Test
    fun `delete should delete order and return empty success message`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!
        val deleteRequest = deleteOrderRequest(order.id.toString())

        // WHEN
        val response = natsPublisher.request(DELETE, deleteRequest, DeleteOrderResponse.parser()).block()

        // THEN
        assertEquals(toDeleteFailureResponse(), response)
        orderRepository.findFullById(order.id.toString())
            .test()
            .verifyComplete()
    }
}
