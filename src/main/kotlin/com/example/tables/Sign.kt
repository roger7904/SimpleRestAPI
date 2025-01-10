package com.example.tables

import org.jetbrains.exposed.sql.Table

object SignTable : Table("sign") {
    val signAccount = varchar("signaccount", 10)
    val signPassword = varchar("signpassword", 10)

    override val primaryKey = PrimaryKey(signAccount, signPassword)
}