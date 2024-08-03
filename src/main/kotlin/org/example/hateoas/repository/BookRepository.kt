package org.example.hateoas.repository

import org.example.hateoas.domain.Book
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : R2dbcRepository<Book, Int>
