package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.controller.nats.order.DeleteOrderNatsController
import com.makarytskyi.rentcar.fixtures.request.OrderProtoFixtures.deleteOrderProtoRequest
import com.makarytskyi.rentcar.proto.reqreply.delete.DeleteOrderProtoResponse
import com.makarytskyi.rentcar.service.OrderService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.nats.client.Connection
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@ExtendWith(MockKExtension::class)
class DeleteOrderNatsControllerTest {

    @MockK
    lateinit var orderService: OrderService

    @MockK
    lateinit var connection: Connection

    @InjectMockKs
    lateinit var deleteController: DeleteOrderNatsController

    @Test
    fun `create should return success message with saved order`() {
        // GIVEN
        val id = ObjectId().toString()
        val protoRequest = deleteOrderProtoRequest(id)

        //WHEN
        every { orderService.deleteById(id) } returns Unit.toMono()

        //THEN
        deleteController.handle(protoRequest)
            .test()
            .assertNext {
                assertEquals(DeleteOrderProtoResponse.ResponseCase.SUCCESS, it.responseCase)
            }
            .verifyComplete()
    }
}
