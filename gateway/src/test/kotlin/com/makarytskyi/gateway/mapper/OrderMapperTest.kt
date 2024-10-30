package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomCreateRequest
import com.makarytskyi.core.fixtures.OrderRequestFixture.randomUpdateRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.aggregatedOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failurePatchResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.randomAggregatedOrder
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulUpdateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.updateOrderResponse
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderMapperTest {

    @Test
    fun `CreateOrderResponse mapper should return order response if response proto is successful`() {
        // GIVEN
        val request = randomCreateRequest()
        val price = 500.0
        val expectedResponse = createOrderResponse(request, price)
        val protoResponse = successfulCreateResponse(request, price)

        // WHEN
        val response = protoResponse.toDto().copy(id = expectedResponse.id)

        // THEN
        assertEquals(response, expectedResponse)
    }

    @Test
    fun `CreateOrderResponse mapper should throw IllegalArgumentException if proto has IllegalArgument failure`() {
        // GIVEN
        val exception = IllegalArgumentException("Dates must be in future")
        val protoResponse = failureCreateResponse(exception)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { protoResponse.toDto() }
    }

    @Test
    fun `CreateOrderResponse mapper should throw NotFoundException if response proto has NotFound failure`() {
        // GIVEN
        val exception = NotFoundException("Car with id is not found")
        val protoResponse = failureCreateResponse(exception)

        // WHEN // THEN
        assertThrows<NotFoundException> { protoResponse.toDto() }
    }

    @Test
    fun `GetByIdOrderResponse mapper should return order response if response proto is successful`() {
        // GIVEN
        val order = randomAggregatedOrder()
        val protoResponse = successfulGetByIdResponse(order)
        val expectedResponse = aggregatedOrderResponse(order)

        // WHEN
        val response = protoResponse.toDto()

        // THEN
        assertEquals(response, expectedResponse)
    }

    @Test
    fun `GetByIdOrderResponse mapper should throw NotFoundException if response proto has NotFound failure`() {
        // GIVEN
        val exception = NotFoundException("Car with id is not found")
        val protoResponse = failureGetByIdResponse(exception)

        // WHEN // THEN
        assertThrows<NotFoundException> { protoResponse.toDto() }
    }

    @Test
    fun `PatchOrderResponse mapper should return order response if response proto is successful`() {
        // GIVEN
        val price = 400.0
        val request = randomUpdateRequest()
        val protoResponse = successfulUpdateResponse(request, price)
        val expectedResponse = updateOrderResponse(request, price).copy(
            id = protoResponse.success.order.id,
            carId = protoResponse.success.order.carId,
            userId = protoResponse.success.order.userId
        )

        // WHEN
        val response = protoResponse.toDto()

        // THEN
        assertEquals(response, expectedResponse)
    }

    @Test
    fun `PatchOrderResponse mapper should throw NotFoundException if response proto has NotFound`() {
        // GIVEN
        val exception = NotFoundException("Car with id is not found")
        val protoResponse = failurePatchResponse(exception)

        // WHEN // THEN
        assertThrows<NotFoundException> { protoResponse.toDto() }
    }

    @Test
    fun `PatchOrderResponse mapper should throw IllegalArgumentException if response proto has IllegalArgument`() {
        // GIVEN
        val exception = IllegalArgumentException("Start date must be before end date")
        val protoResponse = failurePatchResponse(exception)

        // WHEN // THEN
        assertThrows<IllegalArgumentException> { protoResponse.toDto() }
    }
}
