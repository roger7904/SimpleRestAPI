package com.example.repository

import com.example.models.AllBookDTO
import com.example.tables.AllBooksTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class AllBooksRepository {

    fun getAllBooks(): List<AllBookDTO> {
        return transaction {
            AllBooksTable.selectAll().map { row ->
                AllBookDTO(
                    allNo = row[AllBooksTable.allNo],
                    allName = row[AllBooksTable.allName],
                    allAuthor = row[AllBooksTable.allAuthor],
                    allBookFrom = row[AllBooksTable.allBookFrom],
                    allUrl = row[AllBooksTable.allUrl]
                )
            }
        }
    }

    fun addBook(book: AllBookDTO): Int {
        return transaction {
            AllBooksTable.insert {
                it[allName] = book.allName
                it[allAuthor] = book.allAuthor
                it[allBookFrom] = book.allBookFrom
                it[allUrl] = book.allUrl
            } get AllBooksTable.allNo
        }
    }

    fun findBookById(id: Int): AllBookDTO? {
        return transaction {
            AllBooksTable.select { AllBooksTable.allNo eq id }
                .map { row ->
                    AllBookDTO(
                        allNo = row[AllBooksTable.allNo],
                        allName = row[AllBooksTable.allName],
                        allAuthor = row[AllBooksTable.allAuthor],
                        allBookFrom = row[AllBooksTable.allBookFrom],
                        allUrl = row[AllBooksTable.allUrl]
                    )
                }
                .singleOrNull()
        }
    }
}

