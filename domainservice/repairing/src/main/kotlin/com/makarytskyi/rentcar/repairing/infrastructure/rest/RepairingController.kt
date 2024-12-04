package com.makarytskyi.rentcar.repairing.infrastructure.rest

import com.makarytskyi.rentcar.repairing.application.port.input.RepairingServiceInputPort
import com.makarytskyi.rentcar.repairing.domain.DomainRepairing
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.AggregatedRepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.CreateRepairingRequest
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.RepairingResponse
import com.makarytskyi.rentcar.repairing.infrastructure.rest.dto.UpdateRepairingRequest
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toDomain
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toPatch
import com.makarytskyi.rentcar.repairing.infrastructure.rest.mapper.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/repairings")
internal class RepairingController(private val repairingInputPort: RepairingServiceInputPort) {

    @GetMapping()
    fun findFullAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<AggregatedRepairingResponse> = repairingInputPort.findFullAll(page, size).map { it.toResponse() }

    @GetMapping("/status/{status}/car/{carId}")
    fun findByStatusAndCar(
        @PathVariable status: DomainRepairing.RepairingStatus,
        @PathVariable carId: String,
    ): Flux<RepairingResponse> = repairingInputPort.findByStatusAndCar(status, carId).map { it.toResponse() }

    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: DomainRepairing.RepairingStatus): Flux<RepairingResponse> =
        repairingInputPort.findByStatus(status).map { it.toResponse() }

    @GetMapping("/car/{carId}")
    fun findByCarId(@PathVariable carId: String): Flux<RepairingResponse> =
        repairingInputPort.findByCarId(carId).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody repairing: CreateRepairingRequest): Mono<RepairingResponse> =
        repairingInputPort.create(repairing.toDomain()).map { it.toResponse() }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<AggregatedRepairingResponse> =
        repairingInputPort.getFullById(id).map { it.toResponse() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> = repairingInputPort.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: String,
        @Valid @RequestBody repairing: UpdateRepairingRequest
    ): Mono<RepairingResponse> =
        repairingInputPort.patch(id, repairing.toPatch()).map { it.toResponse() }
}
