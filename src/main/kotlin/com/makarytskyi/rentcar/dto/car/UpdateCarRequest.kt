package com.makarytskyi.rentcar.dto.car

import com.makarytskyi.rentcar.model.Car

data class UpdateCarRequest(
    val price: Int?,
    val color: Car.CarColor?
)
