package com.makarytskyi.rentcar.repository

import com.fasterxml.jackson.databind.ObjectMapper
import com.makarytskyi.rentcar.fixtures.CarFixture.carPatch
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.repository.impl.MongoCarRepository
import com.makarytskyi.rentcar.repository.impl.RedisCarRepository
import com.makarytskyi.rentcar.repository.impl.RedisCarRepository.Companion.idRedisKey
import com.makarytskyi.rentcar.repository.impl.RedisCarRepository.Companion.plateRedisKey
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test

internal class RedisCarRepositoryTest : ContainerBase {
    @Autowired
    lateinit var redisCarRepository: RedisCarRepository

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    lateinit var mongoCarRepository: MongoCarRepository

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun `redis repository should cache the car after calling getById`() {
        // GIVEN
        val car = mongoCarRepository.create(randomCar()).block()!!
        val carId = car.id.toString()
        val redisKey = idRedisKey(carId)

        // WHEN
        val redisCar = redisCarRepository.findById(carId).block()

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(redisKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(redisKey)
            .test()
            .assertNext {
                val cachedCar = mapper.readValue(it, MongoCar::class.java)
                assertEquals(redisCar, cachedCar)
            }
            .verifyComplete()
    }

    @Test
    fun `redis repository should return car by id from cache if car is in redis`() {
        // GIVEN
        val car = randomCar()
        val carId = car.id.toString()
        val redisKey = idRedisKey(carId)
        redisTemplate.opsForValue()
            .set(redisKey, mapper.writeValueAsBytes(car), Duration.ofSeconds(TTL_SECONDS)).block()

        // WHEN
        val redisResult = redisCarRepository.findById(carId)

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(redisKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisResult
            .test()
            .expectNext(car)
            .verifyComplete()
    }

    @Test
    fun `redis repository should cache empty byte array by id if it doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val carId = car.id.toString()
        val redisKey = idRedisKey(carId)

        // WHEN
        redisCarRepository.findById(carId).block()

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(redisKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(redisKey)
            .test()
            .assertNext {
                assertTrue(it.isEmpty(), "Redis should cache unexisting cars with empty byte array")
            }
            .verifyComplete()
    }

    @Test
    fun `create should cache car by its id and plate`() {
        // GIVEN
        val car = randomCar()
        val idKey = idRedisKey(car.id.toString())
        val plateKey = plateRedisKey(car.plate!!)

        // WHEN
        redisCarRepository.create(car).block()!!

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(idKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.hasKey(plateKey)
            .test()
            .expectNext(true)
            .verifyComplete()

        redisTemplate.opsForValue().get(idKey)
            .map { mapper.readValue(it, MongoCar::class.java) }
            .test()
            .expectNext(car)
            .verifyComplete()

    }

    @Test
    fun `delete should remove car from redis`() {
        // GIVEN
        val car = redisCarRepository.create(randomCar()).block()!!
        val redisKey = idRedisKey(car.id.toString())

        // WHEN
        redisCarRepository.deleteById(car.id.toString()).block()

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(redisKey)
                    .test()
                    .expectNext(false)
                    .verifyComplete()
            }
    }

    @Test
    fun `patch should replace cached car`() {
        // GIVEN
        val car = redisCarRepository.create(randomCar()).block()!!
        val patch = carPatch(updateCarRequest())
        val idKey = idRedisKey(car.id.toString())
        val plateKey = plateRedisKey(car.plate!!)

        // WHEN
        val updatedCar = redisCarRepository.patch(car.id.toString(), patch).block()!!

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.opsForValue().get(idKey)
                    .map { mapper.readValue(it, MongoCar::class.java) }
                    .test()
                    .expectNext(updatedCar)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(plateKey)
            .map { mapper.readValue(it, MongoCar::class.java) }
            .test()
            .expectNext(updatedCar)
            .verifyComplete()
    }

    @Test
    fun `patch should not replace cached car by plate if it is null`() {
        // GIVEN
        val car = redisCarRepository.create(randomCar().copy(plate = null)).block()!!
        val patch = carPatch(updateCarRequest())
        val plateKey = plateRedisKey(car.plate.toString())

        // WHEN
        redisCarRepository.patch(car.id.toString(), patch).block()!!

        // THEN
        redisTemplate.hasKey(plateKey)
            .test()
            .expectNext(false)
            .verifyComplete()
    }

    @Test
    fun `redis repository should cache the car after calling findByPlate`() {
        // GIVEN
        val car = mongoCarRepository.create(randomCar()).block()!!
        val plate = car.plate!!
        val plateKey = plateRedisKey(plate)

        // WHEN
        redisCarRepository.findByPlate(plate).block()

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(plateKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(plateKey)
            .test()
            .assertNext {
                val cachedCar = mapper.readValue(it, MongoCar::class.java)
                assertEquals(car, cachedCar)
            }
            .verifyComplete()
    }

    @Test
    fun `redis repository should return car by plate from cache if car is in redis`() {
        // GIVEN
        val car = randomCar()
        val plate = car.plate!!
        val plateKey = plateRedisKey(plate)
        redisTemplate.opsForValue()
            .set(plateKey, mapper.writeValueAsBytes(car), Duration.ofSeconds(TTL_SECONDS)).block()

        // WHEN
        val redisResult = redisCarRepository.findByPlate(plate)

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(plateKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisResult
            .test()
            .expectNext(car)
            .verifyComplete()
    }

    @Test
    fun `redis repository cache empty byte array by plate if it doesn't exist`() {
        // GIVEN
        val car = randomCar()
        val plate = car.plate!!
        val plateKey = plateRedisKey(plate)

        // WHEN
        redisCarRepository.findByPlate(plate).block()

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(plateKey)
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(plateKey)
            .test()
            .assertNext {
                assertTrue(it.isEmpty(), "Redis should cache unexisting cars with empty byte array")
            }
            .verifyComplete()
    }

    @Test
    fun `redis repository should not cache car by plate if it is null`() {
        // GIVEN
        val car = randomCar().copy(plate = null)
        val plateKey = plateRedisKey(car.plate.toString())

        // WHEN
        redisCarRepository.create(car).block()

        // THEN
        redisTemplate.hasKey(plateKey)
            .test()
            .expectNext(false)
            .verifyComplete()
    }

    companion object {
        private const val TTL_SECONDS: Long = 5
        private const val AWAIT_SECONDS: Long = 3
    }
}
