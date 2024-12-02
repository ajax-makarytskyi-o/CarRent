package com.makarytskyi.rentcar.order.infrastructure.nats.mapper

import com.makarytskyi.commonmodels.order.AggregatedOrder
import com.makarytskyi.commonmodels.order.OrderUpdate
import com.makarytskyi.core.exception.NotFoundException
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderRequest
import com.makarytskyi.internalapi.input.reqreply.order.CreateOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.DeleteOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.FindAllOrdersResponse
import com.makarytskyi.internalapi.input.reqreply.order.GetByIdOrderResponse
import com.makarytskyi.internalapi.input.reqreply.order.UpdateOrderResponse
import com.makarytskyi.rentcar.common.util.Utils.dateToTimestamp
import com.makarytskyi.rentcar.common.util.Utils.timestampToDate
import com.makarytskyi.rentcar.order.application.mapper.toProto
import com.makarytskyi.rentcar.order.domain.DomainOrder
import com.makarytskyi.rentcar.order.domain.patch.DomainOrderPatch
import com.makarytskyi.rentcar.order.domain.projection.AggregatedDomainOrder

@Suppress("TooManyFunctions")
object OrderProtoMapper {
    fun CreateOrderRequest.toDomain() = DomainOrder(
        id = null,
        carId = order.carId,
        userId = order.userId,
        from = timestampToDate(order.from),
        to = timestampToDate(order.to),
        price = null,
    )

    fun DomainOrder.toCreateResponse(): CreateOrderResponse = CreateOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }
        .build()

    fun List<AggregatedDomainOrder>.toFindAllResponse(): FindAllOrdersResponse = FindAllOrdersResponse.newBuilder()
        .also { it.successBuilder.addAllOrders(this.map { responseDto -> responseDto.toProto() }) }.build()

    fun AggregatedDomainOrder.toGetByIdResponse(): GetByIdOrderResponse = GetByIdOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }.build()

    fun DomainOrder.toPatchResponse(): UpdateOrderResponse = UpdateOrderResponse.newBuilder()
        .also { it.successBuilder.setOrder(this.toProto()) }.build()

    fun toDeleteFailureResponse(): DeleteOrderResponse = DeleteOrderResponse.newBuilder()
        .apply { successBuilder }.build()

    fun OrderUpdate.toPatch(): DomainOrderPatch = DomainOrderPatch(
        from = timestampToDate(startDate),
        to = timestampToDate(endDate),
    )

    fun AggregatedDomainOrder.toProto(): AggregatedOrder = AggregatedOrder.newBuilder()
        .also {
            it.id = id
            it.car = car.toProto()
            it.user = user.toProto()
            it.from = dateToTimestamp(from)
            it.to = dateToTimestamp(to)
            it.price = price?.toDouble() ?: throw IllegalArgumentException("Car price is null")
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

    fun Throwable.toFindAllFailureResponse(): FindAllOrdersResponse =
        FindAllOrdersResponse.newBuilder()
            .also { it.failureBuilder.message = message }
            .build()

    fun Throwable.toGetByIdFailureResponse(): GetByIdOrderResponse =
        GetByIdOrderResponse.newBuilder()
            .also {
                it.failureBuilder.also { failure ->
                    failure.message = message.orEmpty()
                    when (this) {
                        is NotFoundException -> failure.notFoundBuilder
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
}
