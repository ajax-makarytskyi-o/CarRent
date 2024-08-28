package com.makarytskyi.rentcar.controller

import com.makarytskyi.rentcar.dto.repairing.CreateRepairingRequest
import com.makarytskyi.rentcar.dto.repairing.RepairingResponse
import com.makarytskyi.rentcar.dto.repairing.UpdateRepairingRequest
import com.makarytskyi.rentcar.model.Repairing
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

@RestController
@RequestMapping("/api/v1/repairings")
class RepairingController(private val service: RepairingService) {

    @GetMapping()
    fun findAll(
        @RequestParam(required = false) carId: String?,
        @RequestParam(required = false) status: Repairing.RepairingStatus?
    ): List<RepairingResponse> = when {
        carId != null && status != null -> service.findByStatusAndCar(status, carId)
        carId != null -> service.findByCarId(carId)
        status != null -> service.findByStatus(status)
        else -> service.findAll()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody repairing: CreateRepairingRequest): RepairingResponse = service.create(repairing)

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): RepairingResponse = service.getById(id)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String) = service.deleteById(id)

    @PatchMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody repairing: UpdateRepairingRequest): RepairingResponse =
        service.update(id, repairing)
}
