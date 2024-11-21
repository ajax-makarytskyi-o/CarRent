package com.makarytskyi.internalapi.topic

object KafkaTopic {
    object Repairing {
        private const val REPAIRING_BASE = "repairing"
        const val REPAIRING_CREATE = "$REPAIRING_BASE.create"
    }

    object Order {
        private const val ORDER_BASE = "order"
        const val ORDER_CREATE = "$ORDER_BASE.create"
    }

    object User {
        private const val USER_BASE = "user"
        const val NOTIFICATION = "$USER_BASE.notification"
    }
}
