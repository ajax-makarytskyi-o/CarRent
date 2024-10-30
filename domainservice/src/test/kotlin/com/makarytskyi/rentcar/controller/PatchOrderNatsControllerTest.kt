package com.makarytskyi.rentcar.controller

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.PATCH
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.randomUser
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failurePatchResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successfulPatchResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.updateOrderRequest
import com.makarytskyi.rentcar.repository.CarRepository
import com.makarytskyi.rentcar.repository.ContainerBase
import com.makarytskyi.rentcar.repository.OrderRepository
import com.makarytskyi.rentcar.repository.UserRepository
import com.makarytskyi.rentcar.util.timestampToDate
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired

@ExtendWith(MockKExtension::class)
class PatchOrderNatsControllerTest : ContainerBase {

    @Autowired
    lateinit var connection: Connection

    @Autowired
    internal lateinit var carRepository: CarRepository

    @Autowired
    internal lateinit var userRepository: UserRepository

    @Autowired
    internal lateinit var orderRepository: OrderRepository

    @Test
    fun `patch should return success message with updated order`() {
        // GIVEN
        val car = carRepository.create(randomCar()).block()!!
        val user = userRepository.create(randomUser()).block()!!
        val order = orderRepository.create(randomOrder(car.id, user.id)).block()!!

        val protoRequest = updateOrderRequest(order.id.toString())
        val updatedOrder =
            randomOrder(car.id, user.id).copy(
                from = timestampToDate(protoRequest.patch.from),
                to = timestampToDate(protoRequest.patch.to),
            )
        val responseDto = responseOrderDto(updatedOrder, car).copy(id = order.id.toString())
        val protoResponse = successfulPatchResponse(responseDto)

        //WHEN
        val response = connection.request(PATCH, protoRequest.toByteArray())

        //THEN
        val data = PatchOrderResponse.parser().parseFrom(response.get().data)
        assertEquals(protoResponse, data)
    }

    @Test
    fun `create should return error message if updating order isn't found`() {
        // GIVEN
        val id = ObjectId().toString()
        val exception = NotFoundException("Order with id $id is not found")
        val protoRequest = updateOrderRequest(id)
        val protoResponse = failurePatchResponse(exception)

        //WHEN
        val response = connection.request(PATCH, protoRequest.toByteArray())

        //THEN
        val data = PatchOrderResponse.parser().parseFrom(response.get().data)
        assertEquals(protoResponse, data)
    }
}
