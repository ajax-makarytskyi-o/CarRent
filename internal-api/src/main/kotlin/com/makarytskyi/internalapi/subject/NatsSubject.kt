package com.makarytskyi.internalapi.subject

object NatsSubject {
    object Order {
        private const val ORDER_BASE = "order"

        const val CREATE = "$ORDER_BASE.create"
        const val PATCH = "$ORDER_BASE.patch"
        const val DELETE = "$ORDER_BASE.delete"
        const val GET_BY_ID = "$ORDER_BASE.get_by_id"
        const val FIND_ALL = "$ORDER_BASE.find_all"
    }
}
