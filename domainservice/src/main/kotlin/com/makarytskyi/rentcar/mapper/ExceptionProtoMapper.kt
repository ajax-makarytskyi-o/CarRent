package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse

fun Throwable.toFindAllFailureResponse(): FindAllOrdersResponse =
    FindAllOrdersResponse.newBuilder()
        .also { it.failureBuilder.message = this.message }
        .build()

fun Throwable.toGetByIdFailureResponse(): GetByIdOrderResponse =
    GetByIdOrderResponse.newBuilder()
        .also {
            it.failureBuilder.also { failure ->
                failure.message = message.orEmpty()
                failure.notFoundBuilder
            }
        }
        .build()

fun Throwable.toCreateFailureResponse(): CreateOrderResponse =
    CreateOrderResponse.newBuilder()
        .also {
            it.failureBuilder.also { failure ->
                failure.message = message.orEmpty()
                when (this) {
                    is NotFoundException -> failure.notFoundBuilder
                    is IllegalArgumentException -> failure.illegalArgumentBuilder
                }
            }
        }
        .build()

fun Throwable.toPatchFailureResponse(): UpdateOrderResponse =
    UpdateOrderResponse.newBuilder()
        .also {
            it.failureBuilder.also { failure ->
                failure.message = message.orEmpty()
                when (this) {
                    is NotFoundException -> failure.notFoundBuilder
                    is IllegalArgumentException -> failure.illegalArgumentBuilder
                }
            }
        }
        .build()
