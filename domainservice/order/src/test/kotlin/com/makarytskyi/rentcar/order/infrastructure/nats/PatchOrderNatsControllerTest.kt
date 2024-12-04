package com.makarytskyi.rentcar.order.infrastructure.nats

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import com.makarytskyi.internalapi.subject.NatsSubject.Order.UPDATE
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.common.util.Utils.timestampToDate
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.createOrderRequest
import com.makarytskyi.rentcar.fixtures.OrderFixture.randomOrder
import com.makarytskyi.rentcar.fixtures.OrderFixture.responseOrderDto
import com.makarytskyi.rentcar.fixtures.UserFixture.createUserRequest
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.failurePatchResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.successfulPatchResponse
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.updateOrderRequest
import com.makarytskyi.rentcar.order.ContainerBase
import com.makarytskyi.rentcar.order.application.port.output.OrderRepositoryOutputPort
import com.makarytskyi.rentcar.user.application.port.output.UserRepositoryOutputPort
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import systems.ajax.nats.publisher.api.NatsMessagePublisher

class PatchOrderNatsControllerTest : ContainerBase {

    @Autowired
    internal lateinit var natsPublisher: NatsMessagePublisher

    @Autowired
    internal lateinit var carRepository: CarRepositoryOutputPort

    @Autowired
    internal lateinit var userRepository: UserRepositoryOutputPort

    @Autowired
    internal lateinit var orderRepository: OrderRepositoryOutputPort

    @Test
    fun `patch should return success message with updated order`() {
        // GIVEN
        val car = carRepository.create(createCarRequest()).block()!!
        val user = userRepository.create(createUserRequest()).block()!!
        val order = orderRepository.create(createOrderRequest(car.id, user.id)).block()!!

        val protoRequest = updateOrderRequest(order.id)
        val updatedOrder =
            randomOrder(car.id, user.id).copy(
                from = timestampToDate(protoRequest.update.startDate),
                to = timestampToDate(protoRequest.update.endDate),
            )
        val responseDto = responseOrderDto(updatedOrder, car).copy(id = order.id)
        val protoResponse = successfulPatchResponse(responseDto)

        // WHEN
        val response = natsPublisher.request(UPDATE, protoRequest, UpdateOrderResponse.parser()).block()

        // THEN
        assertEquals(protoResponse, response)
    }

    @Test
    fun `patch should return error message if updating order isn't found`() {
        // GIVEN
        val id = ObjectId().toString()
        val exception = NotFoundException("Order with id $id is not found")
        val protoRequest = updateOrderRequest(id)
        val protoResponse = failurePatchResponse(exception)

        // WHEN
        val response = natsPublisher.request(UPDATE, protoRequest, UpdateOrderResponse.parser()).block()

        // THEN
        assertEquals(protoResponse, response)
    }
}
