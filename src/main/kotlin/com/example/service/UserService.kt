package com.example.service

import com.example.models.User
import com.example.repository.UserRepository

class UserService(private val repository: UserRepository) {
    fun createUser(name: String, age: Int): User {
        if (name.isBlank()) throw IllegalArgumentException("Name cannot be blank")
        if (age <= 0) throw IllegalArgumentException("Age must be positive")
        return repository.addUser(name, age)
    }

    fun listUsers(): List<User> {
        return repository.getAllUsers()
    }
}