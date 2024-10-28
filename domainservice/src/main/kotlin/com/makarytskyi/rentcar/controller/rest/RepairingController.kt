package com.makarytskyi.rentcar.controller.rest

import com.makarytskyi.rentcar.dto.repairing.AggregatedRepairingResponse
import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.MongoRepairing
import com.makarytskyi.rentcar.service.RepairingService
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
internal class RepairingController(private val service: RepairingService) {

    @GetMapping()
    fun findFullAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<AggregatedRepairingResponse> = service.findFullAll(page, size)

    @GetMapping("/status/{status}/car/{carId}")
    fun findByStatusAndCar(
        @PathVariable status: MongoRepairing.RepairingStatus,
        @PathVariable carId: String,
    ): Flux<RepairingResponse> = service.findByStatusAndCar(status, carId)

    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: MongoRepairing.RepairingStatus): Flux<RepairingResponse> =
        service.findByStatus(status)

    @GetMapping("/car/{carId}")
    fun findByCarId(@PathVariable carId: String): Flux<RepairingResponse> = service.findByCarId(carId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody repairing: CreateRepairingRequest): Mono<RepairingResponse> =
        service.create(repairing)

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<AggregatedRepairingResponse> = service.getFullById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> = service.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(
        @PathVariable id: String,
        @Valid @RequestBody repairing: UpdateRepairingRequest
    ): Mono<RepairingResponse> =
        service.patch(id, repairing)
}
