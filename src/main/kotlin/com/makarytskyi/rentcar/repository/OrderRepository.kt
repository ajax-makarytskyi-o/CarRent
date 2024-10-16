package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.patch.MongoOrderPatch
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
internal interface OrderRepository {

    fun findFullById(id: String): Mono<AggregatedMongoOrder>

    fun findFullAll(page: Int, size: Int): Flux<AggregatedMongoOrder>

    fun create(mongoOrder: MongoOrder): Mono<MongoOrder>

    fun deleteById(id: String): Mono<Unit>

    fun findByUserId(userId: String): Flux<MongoOrder>

    fun findByCarId(carId: String): Flux<MongoOrder>

    fun patch(id: String, orderPatch: MongoOrderPatch): Mono<MongoOrder>

    fun findByCarIdAndUserId(carId: String, userId: String): Flux<MongoOrder>
}
