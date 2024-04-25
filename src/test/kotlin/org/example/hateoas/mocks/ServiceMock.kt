package org.example.hateoas.mocks

import org.example.hateoas.Book
import org.example.hateoas.BookPagination
import org.example.hateoas.Repository
import org.example.hateoas.Service
import org.mockito.Mockito.mock
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

class ServiceMock(repositoryMock: Repository, templateMock: R2dbcEntityTemplate) :
	Service(repositoryMock, templateMock) {

	override fun create(b: Book): Mono<Book> = just(mock(Book::class.java))

	override fun update(id: Int, b: Book): Mono<Book> = just(mock(Book::class.java))

	override fun get(id: Int): Mono<Book> = just(mock(Book::class.java))

	override fun delete(id: Int): Mono<Void> = empty()

	override fun list(pr: BookPagination): Flux<Book> = Flux.just(mock(Book::class.java))

	override fun count(pr: BookPagination): Mono<Long> = just(0L);
}
