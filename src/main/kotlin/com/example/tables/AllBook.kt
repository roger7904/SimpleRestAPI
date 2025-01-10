package com.example.tables

import org.jetbrains.exposed.sql.Table

object AllBooksTable : Table("allbooks") {
    val allNo = integer("allno").autoIncrement()
    val allName = varchar("allname", 30)
    val allAuthor = varchar("allauthor", 30)
    val allBookFrom = varchar("allbookfrom", 30)
    val allUrl = varchar("allurl", 500)

    override val primaryKey = PrimaryKey(allNo)
}