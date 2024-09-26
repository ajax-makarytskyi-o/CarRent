package com.makarytskyi.rentcar.controller

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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/repairings")
internal class RepairingController(private val service: RepairingService) {

    @GetMapping()
    fun findAll(): List<AggregatedRepairingResponse> = service.findAll()

    @GetMapping("/status/{status}/car/{carId}")
    fun findByStatusAndCar(
        @PathVariable status: MongoRepairing.RepairingStatus,
        @PathVariable carId: String
    ): List<RepairingResponse> = service.findByStatusAndCar(status, carId)

    @GetMapping("/status/{status}")
    fun findByStatus(@PathVariable status: MongoRepairing.RepairingStatus): List<RepairingResponse> =
        service.findByStatus(status)

    @GetMapping("/car/{carId}")
    fun findByCarId(@PathVariable carId: String): List<RepairingResponse> = service.findByCarId(carId)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody repairing: CreateRepairingRequest): RepairingResponse = service.create(repairing)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): AggregatedRepairingResponse = service.getById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String) = service.deleteById(id)

    @PatchMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody repairing: UpdateRepairingRequest): RepairingResponse =
        service.update(id, repairing)
}
