package com.makarytskyi.rentcar.controller

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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
internal class UserController(private val service: UserService) {

    @GetMapping("/{id}")
    fun findById(@PathVariable id: String): UserResponse = service.getById(id)

    @GetMapping()
    fun findAll(): List<UserResponse> = service.findAll()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody user: CreateUserRequest): UserResponse = service.create(user)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteById(@PathVariable id: String) = service.deleteById(id)

    @PatchMapping("/{id}")
    fun update(@PathVariable id: String, @Valid @RequestBody user: UpdateUserRequest): UserResponse =
        service.update(id, user)

    @GetMapping("/email/{email}")
    fun getByEmail(@PathVariable email: String): UserResponse = service.getByEmail(email)

    @GetMapping("/phone/{phone}")
    fun getByPhoneNumber(@PathVariable phone: String): UserResponse = service.getByPhoneNumber(phone)
}
