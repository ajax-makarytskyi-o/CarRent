package com.makarytskyi.rentcar.user.infrastructure.rest

import com.makarytskyi.rentcar.user.application.port.input.UserServiceInputPort
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.CreateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UpdateUserRequest
import com.makarytskyi.rentcar.user.infrastructure.rest.dto.UserResponse
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toDomain
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toPatch
import com.makarytskyi.rentcar.user.infrastructure.rest.mapper.toResponse
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
@RequestMapping("/users")
internal class UserController(private val userInputPort: UserServiceInputPort) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<UserResponse> = userInputPort.getById(id).map { it.toResponse() }

    @GetMapping()
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<UserResponse> = userInputPort.findAll(page, size).map { it.toResponse() }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody user: CreateUserRequest): Mono<UserResponse> =
        userInputPort.create(user.toDomain()).map { it.toResponse() }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> = userInputPort.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody user: UpdateUserRequest): Mono<UserResponse> =
        userInputPort.patch(id, user.toPatch()).map { it.toResponse() }

    @GetMapping("/email/{email}")
    fun getByEmail(@PathVariable email: String): Mono<UserResponse> =
        userInputPort.getByEmail(email).map { it.toResponse() }

    @GetMapping("/phone/{phone}")
    fun getByPhoneNumber(@PathVariable phone: String): Mono<UserResponse> =
        userInputPort.getByPhoneNumber(phone).map { it.toResponse() }
}
