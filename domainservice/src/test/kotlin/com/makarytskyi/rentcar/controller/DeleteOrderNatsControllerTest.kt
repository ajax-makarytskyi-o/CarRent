package com.makarytskyi.rentcar.controller

import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.DELETE
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.deleteOrderRequest
import com.makarytskyi.rentcar.mapper.toDeleteResponse
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class DeleteOrderNatsControllerTest : ContainerBase {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var orderRepository: OrderRepository

    @Test
    fun `delete should delete order and return empty success message`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!
        val deleteRequest = deleteOrderRequest(order.id.toString())

        //WHEN
        val response = connection.request(DELETE, deleteRequest.toByteArray())

        //THEN
        val data = DeleteOrderResponse.parser().parseFrom(response.get().data)
        assertEquals(toDeleteResponse(), data)
        orderRepository.findFullById(order.id.toString())
            .test()
            .verifyComplete()
    }
}
