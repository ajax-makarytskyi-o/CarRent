package com.makarytskyi.rentcar.repository

import com.makarytskyi.rentcar.model.Order
import java.util.Date
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository {

    fun findById(id: String): Order?

    fun findAll(): List<Order>

    fun save(order: Order): Order

    fun deleteById(id: String)

    fun findAllByDate(date: Date): List<Order>

    fun findByUserId(userId: String): List<Order>

    fun findByCarId(carId: String): List<Order>

    fun findByDate(date: Date): List<Order>

    fun updateDates(id: String, from: Date?, to: Date?): Order?
}
