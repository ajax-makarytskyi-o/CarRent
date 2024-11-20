package com.makarytskyi.internalapi.subject

object KafkaTopic {
    private const val REPAIRING_BASE = "repairing"
    private const val ORDER_BASE = "order"
    private const val USER_BASE = "user"

    const val ORDER_CREATE = "$ORDER_BASE.create"
    const val REPAIRING_CREATE = "$REPAIRING_BASE.create"
    const val NOTIFICATION = "$USER_BASE.notification"
}
