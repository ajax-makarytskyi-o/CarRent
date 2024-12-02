package com.makarytskyi.rentcar.fixtures.request

import com.makarytskyi.commonmodels.error.Error
import com.makarytskyi.core.dto.order.OrderResponseDto
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAfter
import com.makarytskyi.rentcar.fixtures.OrderFixture.monthAndDayAfter
import com.makarytskyi.rentcar.common.util.Utils.dateToTimestamp
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.user.domain.DomainUser
import com.makarytskyi.rentcar.user.infrastructure.mongo.entity.MongoUser

object OrderProtoFixtures {
    fun createOrderRequest(mongoCar: DomainCar, mongoUser: DomainUser): CreateOrderRequest =
        CreateOrderRequest.newBuilder()
            .apply {
                with(orderBuilder) {
                    carId = mongoCar.id.toString()
                    userId = mongoUser.id.toString()
                    from = dateToTimestamp(monthAfter)
                    to = dateToTimestamp(monthAndDayAfter)
                }
            }
            .build()

    fun successfulCreateResponse(response: CreateOrderRequest, price: Double): CreateOrderResponse = CreateOrderResponse
        .newBuilder()
        .apply {
            with(successBuilder.orderBuilder) {
                setId(response.order.id)
                setCarId(response.order.carId)
                setUserId(response.order.userId)
                setFrom(response.order.from)
                setTo(response.order.to)
                setPrice(price)
            }
        }
        .build()

    fun failureCreateResponse(exception: Exception): CreateOrderResponse = CreateOrderResponse.newBuilder()
        .apply {
            with(failureBuilder) {
                setMessage(exception.message)
                when (exception) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build()

    fun successfulPatchResponse(response: DomainOrder): UpdateOrderResponse = UpdateOrderResponse
        .newBuilder()
        .apply {
            with(successBuilder.orderBuilder) {
                setId(response.id)
                setCarId(response.carId)
                setUserId(response.userId)
                setFrom(dateToTimestamp(response.from!!))
                setTo(dateToTimestamp(response.to!!))
                setPrice(response.price!!.toDouble())
            }
        }
        .build()

    fun failurePatchResponse(exception: Exception): UpdateOrderResponse = UpdateOrderResponse.newBuilder()
        .apply {
            with(failureBuilder) {
                setMessage(exception.message)
                when (exception) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build()

    fun updateOrderRequest(id: String): UpdateOrderRequest =
        UpdateOrderRequest.newBuilder()
            .apply {
                setId(id)
                updateBuilder.setStartDate(dateToTimestamp(monthAfter))
                updateBuilder.setEndDate(dateToTimestamp(monthAndDayAfter))
            }
            .build()

    fun getByIdOrderRequest(id: String): GetByIdOrderRequest = GetByIdOrderRequest
        .newBuilder()
        .setId(id)
        .build()

    fun failureGetByIdResponse(exception: Exception): GetByIdOrderResponse =
        GetByIdOrderResponse.newBuilder()
            .apply {
                with(failureBuilder) {
                    setMessage(exception.message)
                    failureBuilder.notFoundBuilder
                }
            }
            .build()

    fun deleteOrderRequest(id: String): DeleteOrderRequest =
        DeleteOrderRequest.newBuilder()
            .setId(id)
            .build()

    fun findAllOrderRequest(page: Int, size: Int): FindAllOrdersRequest = FindAllOrdersRequest
        .newBuilder()
        .setPage(page)
        .setSize(size)
        .build()
}
