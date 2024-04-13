package org.example.hateoas

import org.example.hateoas.errors.InternalServerException
import org.example.hateoas.errors.NotFoundException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class Service(private val repo: Repository, private val c: DatabaseClient, private val t: R2dbcEntityTemplate) {

	fun create(b: Book) = repo.save(b.copy(dtCreatedAt = LocalDate.now()))
		.doOnError { throw InternalServerException(it) }

	fun update(b: Book) = repo.save(b.copy(dtUpdatedAt = LocalDate.now()))
		.doOnError { throw InternalServerException(it) }

	fun get(id: Int) = repo.findById(id)
		.doOnNext { if (it == null) throw NotFoundException("Não foi possível encontrar nenhum livro com id $id") }

	fun delete(id: Int) = repo.deleteById(id)

	fun list(pr: BookPagination): Flux<Book> =
		t.select(Query.query(pr.buildCriteria()).with(pr.toPageRequest()), Book::class.java)

	fun count(pr: BookPagination): Mono<Long> = t.count(Query.query(pr.buildCriteria()), Book::class.java)
}
