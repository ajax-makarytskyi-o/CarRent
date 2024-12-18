package com.makarytskyi.rentcar

import io.mongock.runner.springboot.EnableMongock
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableMongock
@SpringBootApplication
class RentCarApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<RentCarApplication>(*args)
}
