package com.makarytskyi.internalapi.subject

object NatsSubject {
    object Order {
        const val ORDER_BASE = "order"
        const val CREATE = ORDER_BASE + ".create"
        const val PATCH = ORDER_BASE + ".patch"
        const val DELETE = ORDER_BASE + ".delete"
        const val FIND_BY_ID = ORDER_BASE + ".find_by_id"
        const val FIND_ALL = ORDER_BASE + ".find_all"
    }
}
