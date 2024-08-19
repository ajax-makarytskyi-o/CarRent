package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Order
import com.makarytskyi.rentcar.repository.OrderRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class SimpleOrderRepository: OrderRepository {
    val map: MutableMap<String, Order> = HashMap()

    override fun findById(id: String?): Order? = map[id]

    override fun findAll(): List<Order> = map.values.toList()

    override fun save(order: Order): Order {
        val id = ObjectId().toString()
        val savedOrder = Order(
            id,
            order.carId,
            order.userId,
            order.from,
            order.to
        )

        map[id] = savedOrder
        return savedOrder
    }

    override fun deleteById(id: String?): Order? = map.remove(id)

    override fun findAllByDate(date: Date): List<Order> = map.values.filter { it.from != null && it.to != null }.filter { date.before(it.to) && date.after(it.from) }

    override fun findByUserId(userId: String): List<Order> = map.values.filter { it.userId == userId}

    override fun findByCarId(carId: String): List<Order> = map.values.filter { it.carId == carId }

    override fun updateDates(id: String, from: Date?, to: Date?): Order? {
        val oldOrder: Order? = findById(id)

        if (oldOrder == null) {
            return null
        } else {
            val updatedOrder = Order(
                id,
                oldOrder.carId,
                oldOrder.userId,
                from,
                to
            )

            map[id] = updatedOrder
            return updatedOrder
        }
    }

    override fun findByDate(date: Date): List<Order> = map.values.filter { it.from?.before(date) == true && it.to?.after(date) == true }.toList()
}
