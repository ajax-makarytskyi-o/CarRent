package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.FIND_BY_ID
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.aggregatedOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseAggregatedOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failureGetByIdResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.getByIdOrderRequest
import com.makarytskyi.rentcar.mapper.toProto
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(MockKExtension::class)
class GetByIdOrderNatsControllerTest : ContainerBase {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var orderRepository: OrderRepository

    @Test
    fun `getById should return success message with found order`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!
        val aggregatedOrder = aggregatedOrder(order, car, user)

        val getByIdRequest = getByIdOrderRequest(order.id.toString())
        val expectedOrder = responseAggregatedOrderDto(aggregatedOrder, car).toProto()

        //WHEN
        val response = connection.request(FIND_BY_ID, getByIdRequest.toByteArray())

        //THEN
        val responseOrders = GetByIdOrderResponse.parser().parseFrom(response.get().data)
        assertEquals(responseOrders.success.order, expectedOrder)
    }

    @Test
    fun `getById should return error message if order isn't found`() {
        // GIVEN
        val id = ObjectId().toString()
        val protoRequest = getByIdOrderRequest(id)
        val exception = NotFoundException("Order with id $id is not found")
        val protoResponse = failureGetByIdResponse(exception)

        //WHEN
        val response = connection.request(FIND_BY_ID, protoRequest.toByteArray())

        //THEN
        val responseOrder = GetByIdOrderResponse.parser().parseFrom(response.get().data)
        assertEquals(protoResponse, responseOrder)
    }
}
