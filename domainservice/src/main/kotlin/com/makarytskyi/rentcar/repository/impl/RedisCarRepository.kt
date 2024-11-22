package com.makarytskyi.rentcar.repository.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.makarytskyi.rentcar.model.MongoCar
import com.makarytskyi.rentcar.model.patch.MongoCarPatch
import com.makarytskyi.rentcar.repository.CarRepository
import io.lettuce.core.RedisException
import java.net.SocketException
import java.time.Duration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
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
internal class RedisCarRepository(
    @Qualifier("mongoCarRepository")
    private val mongoCarRepository: CarRepository,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, ByteArray>,
    private val mapper: ObjectMapper,
    @Value("\${redis.key-ttl}")
    private val ttlSeconds: Long,
    @Value("\${redis.retries}")
    private val retries: Long,
) : CarRepository by mongoCarRepository {

    override fun findById(id: String): Mono<MongoCar> =
        reactiveRedisTemplate.opsForValue().get(idRedisKey(id))
            .map { mapper.readValue(it, MongoCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                mongoCarRepository.findById(id)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext {
                        setRedisKey(idRedisKey(id), mapper.writeValueAsBytes(it), ttlSeconds)
                    }
                    .switchIfEmpty {
                        setRedisKey(idRedisKey(id), byteArrayOf(), ttlSeconds)
                        Mono.empty()
                    }
            }

    override fun create(mongoCar: MongoCar): Mono<MongoCar> =
        mongoCarRepository.create(mongoCar)
            .doOnNext { car ->
                val operations = mutableMapOf(idRedisKey(car.id.toString()) to mapper.writeValueAsBytes(car))
                car.plate?.let { plate -> operations[plateRedisKey(plate)] = mapper.writeValueAsBytes(car) }
                reactiveRedisTemplate.opsForValue()
                    .multiSet(operations)
                    .subscribeOn(Schedulers.boundedElastic())
                    .retryWhen(retryOnRedisError())
                    .doOnSuccess {
                        setExpirationOnKey(idRedisKey(car.id.toString()), ttlSeconds)
                        car.plate?.let { plate -> setExpirationOnKey(plateRedisKey(plate), ttlSeconds) }
                    }
                    .subscribe()
            }

    override fun deleteById(id: String): Mono<Unit> =
        mongoCarRepository.deleteById(id)
            .doOnSuccess {
                reactiveRedisTemplate.opsForValue()
                    .delete(idRedisKey(id))
                    .subscribeOn(Schedulers.boundedElastic())
                    .retryWhen(retryOnRedisError())
                    .subscribe()
            }

    override fun patch(id: String, carPatch: MongoCarPatch): Mono<MongoCar> =
        mongoCarRepository.patch(id, carPatch)
            .doOnNext { car ->
                val operations = mutableMapOf(idRedisKey(car.id.toString()) to mapper.writeValueAsBytes(car))
                car.plate?.let { plate -> operations[plateRedisKey(plate)] = mapper.writeValueAsBytes(car) }
                reactiveRedisTemplate.opsForValue()
                    .multiSet(operations)
                    .subscribeOn(Schedulers.boundedElastic())
                    .retryWhen(retryOnRedisError())
                    .doOnSuccess {
                        setExpirationOnKey(idRedisKey(car.id.toString()), ttlSeconds)
                        car.plate?.let { plate -> setExpirationOnKey(plateRedisKey(plate), ttlSeconds) }
                    }
                    .subscribe()
            }

    override fun findByPlate(plate: String): Mono<MongoCar> =
        reactiveRedisTemplate.opsForValue().get(plateRedisKey(plate))
            .map { mapper.readValue(it, MongoCar::class.java) }
            .onErrorResume { Mono.empty() }
            .switchIfEmpty {
                mongoCarRepository.findByPlate(plate)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext {
                        setRedisKey(plateRedisKey(plate), mapper.writeValueAsBytes(it), ttlSeconds)
                    }
                    .switchIfEmpty {
                        setRedisKey(plateRedisKey(plate), byteArrayOf(), ttlSeconds)
                        Mono.empty()
                    }
            }

    private fun retryOnRedisError() =
        Retry.fixedDelay(retries, Duration.ofSeconds(2))
            .filter { throwable -> throwable::class in setOf(
                    SocketException::class,
                    RedisException::class,
                    RedisConnectionFailureException::class,
                    QueryTimeoutException::class,
                )
            }

    private fun setExpirationOnKey(key: String, ttlSeconds: Long) =
        reactiveRedisTemplate
            .expire(key, Duration.ofSeconds(ttlSeconds))
            .retryWhen(retryOnRedisError())
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()

    private fun setRedisKey(key: String, value: ByteArray, ttlSeconds: Long) =
        reactiveRedisTemplate.opsForValue()
            .set(key, value, Duration.ofSeconds(ttlSeconds))
            .subscribeOn(Schedulers.boundedElastic())
            .retryWhen(retryOnRedisError())
            .onErrorResume { Mono.empty() }
            .subscribe()

    companion object {
        private const val KEY_CAR_PREFIX = "car-redis-prefix"
        fun idRedisKey(id: String): String = "$KEY_CAR_PREFIX-$id"
        fun plateRedisKey(plate: String): String = "$KEY_CAR_PREFIX-$plate"
    }
}
