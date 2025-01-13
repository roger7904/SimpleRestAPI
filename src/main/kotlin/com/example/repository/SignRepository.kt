package com.example.repository

import com.example.models.SignDTO
import com.example.tables.SignTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class SignRepository {

    fun getAllSign(): List<SignDTO> {
        return transaction {
            SignTable.selectAll().map { row ->
                SignDTO(
                    signAccount = row[SignTable.signAccount],
                    signPassword = row[SignTable.signPassword]
                )
            }
        }
    }

    fun findByAccountPassword(account: String, password: String): SignDTO? {
        return transaction {
            SignTable.select {
                (SignTable.signAccount eq account) and (SignTable.signPassword eq password)
            }.map { row ->
                SignDTO(
                    signAccount = row[SignTable.signAccount],
                    signPassword = row[SignTable.signPassword]
                )
            }.singleOrNull()
        }
    }
}

