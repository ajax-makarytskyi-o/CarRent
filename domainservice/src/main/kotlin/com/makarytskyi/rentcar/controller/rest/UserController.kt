package com.makarytskyi.rentcar.controller.rest

import com.makarytskyi.rentcar.dto.user.CreateUserRequest
import com.makarytskyi.rentcar.dto.user.UpdateUserRequest
import com.makarytskyi.rentcar.dto.user.UserResponse
import com.makarytskyi.rentcar.service.UserService
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
internal class UserController(private val service: UserService) {

    @GetMapping("/{id}")
    fun getById(@PathVariable id: String): Mono<UserResponse> = service.getById(id)

    @GetMapping()
    fun findAll(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int
    ): Flux<UserResponse> = service.findAll(page, size)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody user: CreateUserRequest): Mono<UserResponse> = service.create(user)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String): Mono<Unit> = service.deleteById(id)

    @PatchMapping("/{id}")
    fun patch(@PathVariable id: String, @Valid @RequestBody user: UpdateUserRequest): Mono<UserResponse> =
        service.patch(id, user)

    @GetMapping("/email/{email}")
    fun getByEmail(@PathVariable email: String): Mono<UserResponse> = service.getByEmail(email)

    @GetMapping("/phone/{phone}")
    fun getByPhoneNumber(@PathVariable phone: String): Mono<UserResponse> = service.getByPhoneNumber(phone)
}
