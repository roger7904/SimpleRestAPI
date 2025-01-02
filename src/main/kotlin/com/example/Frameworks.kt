package com.example

import com.example.repository.UserRepository
import com.example.service.UserService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    val appModule = module {
        single { UserRepository() }
        single { UserService(get()) }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
