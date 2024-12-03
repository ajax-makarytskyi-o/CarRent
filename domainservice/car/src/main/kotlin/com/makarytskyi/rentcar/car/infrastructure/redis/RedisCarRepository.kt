package com.makarytskyi.rentcar.car.infrastructure.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.makarytskyi.rentcar.car.application.port.output.CarOutputPort
import com.makarytskyi.rentcar.car.domain.DomainCar
import com.makarytskyi.rentcar.common.config.RedisProperties
import io.lettuce.core.RedisException
import java.net.SocketException
import java.time.Duration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.dao.QueryTimeoutException
import org.springframework.data.redis.RedisConnectionFailureException
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.util.retry.Retry

@Repository
@Primary
class RedisCarRepository(
    @Qualifier("mongoCarRepository")
    private val mongoCarRepository: CarOutputPort,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val mapper: ObjectMapper,
    private val redisProperties: RedisProperties,
) : CarOutputPort by mongoCarRepository {

    override fun findById(id: String): Mono<DomainCar> =
        reactiveRedisTemplate.opsForValue().get(idRedisKey(id))
            .map { mapper.readValue(it, DomainCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                findByIdInMongoAndCache(id)
            }

    override fun create(car: DomainCar): Mono<DomainCar> =
        mongoCarRepository.create(car)
            .doOnNext { createdCar ->
                setRedisKey(
                    idRedisKey(createdCar.id.toString()),
                    mapper.writeValueAsBytes(createdCar),
                    redisProperties.ttlSeconds
                )
                setRedisKey(
                    plateRedisKey(car.plate),
                    mapper.writeValueAsBytes(car),
                    redisProperties.ttlSeconds
                )

            }

    override fun deleteById(id: String): Mono<Unit> =
        mongoCarRepository.deleteById(id)
            .doOnSuccess {
                reactiveRedisTemplate.unlink(idRedisKey(id))
                    .subscribeOn(Schedulers.boundedElastic())
                    .retryWhen(retryOnRedisError())
                    .subscribe()
            }

    override fun patch(id: String, patch: DomainCar): Mono<DomainCar> =
        findById(id)
            .flatMap { mongoCarRepository.patch(id, patch) }
            .doOnNext { car ->
                setRedisKey(idRedisKey(car.id.toString()), mapper.writeValueAsBytes(car), redisProperties.ttlSeconds)
                setRedisKey(
                    plateRedisKey(car.plate),
                    mapper.writeValueAsBytes(car),
                    redisProperties.ttlSeconds
                )
            }

    override fun findByPlate(plate: String): Mono<DomainCar> =
        reactiveRedisTemplate.opsForValue().get(plateRedisKey(plate))
            .map { mapper.readValue(it, DomainCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                findByPlateInMongoAndCache(plate)
            }

    private fun findByIdInMongoAndCache(id: String): Mono<DomainCar> =
        mongoCarRepository.findById(id)
            .doOnNext {
                setRedisKey(idRedisKey(id), mapper.writeValueAsBytes(it), redisProperties.ttlSeconds)
            }
            .switchIfEmpty {
                setRedisKey(idRedisKey(id), byteArrayOf(), redisProperties.ttlSeconds)
                Mono.empty()
            }

    private fun findByPlateInMongoAndCache(plate: String): Mono<DomainCar> =
        mongoCarRepository.findByPlate(plate)
            .doOnNext {
                setRedisKey(plateRedisKey(plate), mapper.writeValueAsBytes(it), redisProperties.ttlSeconds)
            }
            .switchIfEmpty {
                setRedisKey(plateRedisKey(plate), byteArrayOf(), redisProperties.ttlSeconds)
                Mono.empty()
            }

    private fun retryOnRedisError() =
        Retry.backoff(redisProperties.retries, Duration.ofSeconds(redisProperties.retryTimeout))
            .filter { throwable -> throwable::class in redisErrorSet }

    private fun setRedisKey(key: String, value: ByteArray, ttlSeconds: Long) =
        reactiveRedisTemplate.opsForValue()
            .set(key, value, Duration.ofSeconds(ttlSeconds))
            .subscribeOn(Schedulers.boundedElastic())
            .retryWhen(retryOnRedisError())
            .subscribe()

    companion object {
        private const val KEY_CAR_PREFIX = "car-redis-prefix"
        fun idRedisKey(id: String): String = "$KEY_CAR_PREFIX-$id"
        fun plateRedisKey(plate: String): String = "$KEY_CAR_PREFIX-$plate"

        val redisErrorSet = hashSetOf(
            SocketException::class,
            RedisException::class,
            RedisConnectionFailureException::class,
            QueryTimeoutException::class,
        )
    }
}
