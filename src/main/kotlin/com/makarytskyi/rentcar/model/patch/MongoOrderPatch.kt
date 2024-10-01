package com.makarytskyi.rentcar.model.patch

import java.util.Date

data class MongoOrderPatch(
    val from: Date?,
    val to: Date?,
)
