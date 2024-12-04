package com.makarytskyi.rentcar.car.infrastructure.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.makarytskyi.rentcar.car.ContainerBase
import com.makarytskyi.rentcar.car.application.port.output.CarRepositoryOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.car.infrastructure.redis.RedisCarRepository.Companion.idRedisKey
import com.makarytskyi.rentcar.car.infrastructure.redis.RedisCarRepository.Companion.plateRedisKey
import com.makarytskyi.rentcar.fixtures.CarFixture.createCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.createdCar
import com.makarytskyi.rentcar.fixtures.CarFixture.randomCar
import com.makarytskyi.rentcar.fixtures.CarFixture.updateCarRequest
import com.makarytskyi.rentcar.fixtures.CarFixture.updateDomainCar
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.kotlin.test.test

internal class RedisCarRepositoryTest : ContainerBase {
    @Autowired
    lateinit var redisCarRepository: CarRepositoryOutputPort

    @Autowired
    lateinit var redisTemplate: ReactiveRedisTemplate<String, ByteArray>

    @Autowired
    @Qualifier("mongoCarRepository")
    lateinit var mongoCarRepository: CarRepositoryOutputPort

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun `redis repository should cache the car after calling getById`() {
        // GIVEN
        val car = mongoCarRepository.create(createCarRequest()).block()!!
        val carId = car.id
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
                val cachedCar = mapper.readValue(it, DomainCar::class.java)
                assertEquals(redisCar, cachedCar)
            }
            .verifyComplete()
    }

    @Test
    fun `redis repository should return car by id from cache if car is in redis`() {
        // GIVEN
        val car = randomCar()
        val carId = car.id
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
        val carId = car.id
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
        val request = createCarRequest()
        val car = createdCar(request)
        val plateKey = plateRedisKey(car.plate)

        // WHEN
        val createdCar = redisCarRepository.create(request).block()!!

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.hasKey(idRedisKey(createdCar.id))
                    .test()
                    .expectNext(true)
                    .verifyComplete()
            }

        redisTemplate.hasKey(plateKey)
            .test()
            .expectNext(true)
            .verifyComplete()

        redisTemplate.opsForValue().get(idRedisKey(createdCar.id))
            .map { mapper.readValue(it, DomainCar::class.java) }
            .test()
            .assertNext {
                assertEquals(car.copy(id = it.id), it)
            }
            .verifyComplete()

    }

    @Test
    fun `delete should remove car from redis`() {
        // GIVEN
        val car = redisCarRepository.create(createCarRequest()).block()!!
        val redisKey = idRedisKey(car.id)

        // WHEN
        redisCarRepository.deleteById(car.id).block()

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
        val car = redisCarRepository.create(createCarRequest()).block()!!
        val patch = updateDomainCar(updateCarRequest(), car)
        val idKey = idRedisKey(car.id)
        val plateKey = plateRedisKey(car.plate)

        // WHEN
        val updatedCar = redisCarRepository.patch(car.id, patch).block()!!

        // THEN
        await()
            .atMost(Duration.ofSeconds(AWAIT_SECONDS))
            .untilAsserted {
                redisTemplate.opsForValue().get(idKey)
                    .map { mapper.readValue(it, DomainCar::class.java) }
                    .test()
                    .expectNext(updatedCar)
                    .verifyComplete()
            }

        redisTemplate.opsForValue().get(plateKey)
            .map { mapper.readValue(it, DomainCar::class.java) }
            .test()
            .expectNext(updatedCar)
            .verifyComplete()
    }

    @Test
    fun `redis repository should cache the car after calling findByPlate`() {
        // GIVEN
        val car = mongoCarRepository.create(createCarRequest()).block()!!
        val plate = car.plate
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
                val cachedCar = mapper.readValue(it, DomainCar::class.java)
                assertEquals(car, cachedCar)
            }
            .verifyComplete()
    }

    @Test
    fun `redis repository should return car by plate from cache if car is in redis`() {
        // GIVEN
        val car = randomCar()
        val plate = car.plate
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
        val plate = car.plate
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

    companion object {
        private const val TTL_SECONDS: Long = 5
        private const val AWAIT_SECONDS: Long = 3
    }
}
