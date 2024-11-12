package com.makarytskyi.internalapi.subject

object KafkaTopic {
    private const val REPAIRING_BASE = "repairing"
    private const val USER_BASE = "user"

    const val REPAIRING_CREATE = "$REPAIRING_BASE.create"
    const val NOTIFICATION = "$USER_BASE.notification"
}
