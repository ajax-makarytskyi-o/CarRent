package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.MongoOrder
import com.makarytskyi.rentcar.model.projection.AggregatedMongoOrder
import org.springframework.stereotype.Repository

@Repository
internal interface OrderRepository {

    fun findById(id: String): AggregatedMongoOrder?

    fun findAll(page: Int, size: Int): List<AggregatedMongoOrder>

    fun create(mongoOrder: MongoOrder): MongoOrder

    fun deleteById(id: String)

    fun findByUserId(userId: String): List<MongoOrder>

    fun findByCarId(carId: String): List<MongoOrder>

    fun patch(id: String, mongoOrder: MongoOrder): MongoOrder?

    fun findByCarIdAndUserId(carId: String, userId: String): List<MongoOrder>
}
