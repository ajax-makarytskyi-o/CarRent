package com.makarytskyi.gateway.mapper

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.aggregatedOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createOrderResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.createRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failureGetFullByIdRandomResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.failurePatchResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.getFullByIdRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.grpcGetFullByIdRequest
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.randomAggregatedOrder
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateRandomResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGetFullByIdRandomResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGrpcCreateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulGrpcGetByIdResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.successfulUpdateResponse
import com.makarytskyi.gateway.fixtures.OrderProtoFixture.updateOrderResponse
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.grpcCreateRequest
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.randomCreateRequest
import com.makarytskyi.gateway.fixtures.request.OrderRequestFixture.randomUpdateRequest
import com.makarytskyi.gateway.mapper.OrderMapper.toDto
import com.makarytskyi.gateway.mapper.OrderMapper.toGrpcProto
import com.makarytskyi.gateway.mapper.OrderMapper.toInternalProto
import kotlin.test.assertEquals
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

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

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    fun `CreateOrderResponse mapper should throw exception if proto has failure`(exception: Exception) {
        // GIVEN
        val protoResponse = failureCreateResponse(exception)

        // WHEN // THEN
        assertThrows(exception::class.java) { protoResponse.toDto() }
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
        assertThrows(NotFoundException::class.java) { protoResponse.toDto() }
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

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    fun `PatchOrderResponse mapper should throw exception if proto has failure`(exception: Exception) {
        // GIVEN
        val protoResponse = failurePatchResponse(exception)

        // WHEN // THEN
        assertThrows(exception::class.java) { protoResponse.toDto() }
    }

    @Test
    fun `grpc proto request mapper should return internal proto request`() {
        // GIVEN
        val id = ObjectId().toString()
        val grpcRequest = grpcGetFullByIdRequest(id)
        val internalRequest = getFullByIdRequest(id)

        // WHEN
        val result = grpcRequest.toInternalProto()

        // THEN
        assertEquals(internalRequest, result)
    }

    @Test
    fun `internal getFullById mapper should return grpc response mapper if response is successful`() {
        // GIVEN
        val internalResponse = successfulGetFullByIdRandomResponse()
        val grpcResponse = successfulGrpcGetByIdResponse(internalResponse.success.order)

        // WHEN
        val result = internalResponse.toGrpcProto()

        // THEN
        assertEquals(grpcResponse, result)
    }

    @Test
    fun `internal getFullById mapper should throw exception if response is failure`() {
        // GIVEN
        val exception = NotFoundException("Order is not found")
        val internalResponse = failureGetFullByIdRandomResponse(exception)

        // WHEN // THEN
        assertThrows<NotFoundException> { internalResponse.toGrpcProto() }
    }

    @Test
    fun `grpc create mapper should return internal proto`() {
        // GIVEN
        val dtoRequest = randomCreateRequest()
        val grpcRequest = grpcCreateRequest(dtoRequest)
        val internalResponse = createRequest(dtoRequest)

        // WHEN
        val result = grpcRequest.toInternalProto()

        // THEN
        assertEquals(internalResponse, result)
    }

    @Test
    fun `grpc create response mapper should return internal response if response is successful`() {
        // GIVEN
        val internalResponse = successfulCreateRandomResponse()
        val grpcResponse = successfulGrpcCreateResponse(internalResponse.success.order)

        // WHEN
        val result = internalResponse.toGrpcProto()

        // THEN
        assertEquals(grpcResponse, result)
    }

    @ParameterizedTest
    @MethodSource("exceptionProvider")
    fun `grpc create response mapper should throw exception if response is failure`(exception: Exception) {
        // GIVEN
        val internalResponse = failureCreateResponse(exception)

        // WHEN // THEN
        assertThrows(exception::class.java) { internalResponse.toGrpcProto() }
    }

    companion object {
        @JvmStatic
        fun exceptionProvider() = listOf(
            arrayOf(NotFoundException("Resource with id is not found")),
            arrayOf(IllegalArgumentException("Start date must be before end date"))
        )
    }
}
