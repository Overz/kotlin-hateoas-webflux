package org.example.hateoas.repositories

import org.example.hateoas.models.Book
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : R2dbcRepository<Book, Int>
