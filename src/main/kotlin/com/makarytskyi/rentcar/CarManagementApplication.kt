package com.makarytskyi.rentcar

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarManagementApplication

fun main(args: Array<String>) {
	runApplication<CarManagementApplication>(*args)
}
