package org.example.hateoas.services

import org.example.hateoas.errors.InternalServerException
import org.example.hateoas.models.Book
import org.example.hateoas.repositories.BookRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class BookService(private val repo: BookRepository) {

	fun list(): Flux<Book> {
		return repo.findAll()
	}

	fun create(b: Book): Mono<Book> {
		return repo.save(b.copy(dtCreatedAt = LocalDateTime.now()))
			.doOnError { throw InternalServerException(it) }
			.doOnSuccess { book -> println("Dado persistido: $book") }
	}
}
