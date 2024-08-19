package com.makarytskyi.rentcar.repository.impl

import com.makarytskyi.rentcar.model.Order
import com.makarytskyi.rentcar.repository.OrderRepository
import java.util.Date
import org.bson.types.ObjectId
import org.springframework.stereotype.Repository

@Repository
class SimpleOrderRepository : OrderRepository {
    val map: MutableMap<String, Order> = HashMap()

    override fun findById(id: String): Order? = map[id]

    override fun findAll(): List<Order> = map.values.toList()

    override fun save(order: Order): Order {
        val id = ObjectId().toString()
        val savedOrder = order.copy(id = id)
        map[id] = savedOrder
        return savedOrder
    }

    override fun deleteById(id: String) {
        map.remove(id)
    }

    override fun findAllByDate(date: Date): List<Order> = map.values.filter { it.from != null && it.to != null }.filter { date.before(it.to) && date.after(it.from) }

    override fun findByUserId(userId: String): List<Order> = map.values.filter { it.userId == userId}

    override fun findByCarId(carId: String): List<Order> = map.values.filter { it.carId == carId }

    override fun updateDates(id: String, from: Date?, to: Date?): Order? {
        val oldOrder: Order? = findById(id)

        return oldOrder?.let {
            val updatedOrder = oldOrder.copy(id = id, from = from, to = to)
            map[id] = updatedOrder
            return updatedOrder
        }
    }

    override fun findByDate(date: Date): List<Order> =
        map.values.filter { it.from?.before(date) == true && it.to?.after(date) == true }.toList()
}
