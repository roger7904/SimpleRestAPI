package com.example

import com.example.repository.AllBooksRepository
import com.example.repository.UserAccountRepository
import com.example.repository.UserRepository
import com.example.service.AuthService
import com.example.service.UserService
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureFrameworks() {
    val appModule = module {
        single { UserRepository() }
        single { UserService(get()) }
        single { UserAccountRepository() }
        single { AuthService(get()) }
        single { AllBooksRepository() }
    }

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}
