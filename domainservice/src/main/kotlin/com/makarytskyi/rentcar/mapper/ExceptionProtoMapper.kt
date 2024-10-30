package com.makarytskyi.rentcar.mapper

import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.commonmodels.error.Error
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.PatchOrderResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

fun Throwable.toFindAllResponse(): Mono<FindAllOrdersResponse> =
    FindAllOrdersResponse.newBuilder()
        .apply { failureBuilder.apply { setMessage(this@toFindAllResponse.message) } }
        .build().toMono()

fun Throwable.toGetByIdResponse(): Mono<GetByIdOrderResponse> =
    GetByIdOrderResponse.newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(this@toGetByIdResponse.message)
                when (this@toGetByIdResponse) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                }
            }
        }
        .build().toMono()

fun Throwable.toCreateResponse(): Mono<CreateOrderResponse> =
    CreateOrderResponse.newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(this@toCreateResponse.message)
                when (this@toCreateResponse) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build().toMono()

fun Throwable.toPatchResponse(): Mono<PatchOrderResponse> =
    PatchOrderResponse.newBuilder()
        .apply {
            failureBuilder.apply {
                setMessage(this@toPatchResponse.message)
                when (this@toPatchResponse) {
                    is NotFoundException -> setNotFound(Error.getDefaultInstance())
                    is IllegalArgumentException -> setIllegalArgument(Error.getDefaultInstance())
                }
            }
        }
        .build().toMono()
