package org.example.hateoas

import org.example.hateoas.errors.InternalServerException
import org.example.hateoas.errors.NotFoundException
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class Service(private val repo: Repository, private val t: R2dbcEntityTemplate) {

	fun create(b: Book) = repo.save(b)
		.onErrorMap { InternalServerException(it) }

	fun update(id: Int, b: Book) =
		repo.findById(id)
			.switchIfEmpty(Mono.error(NotFoundException("Nenhum item encontrado com id '$id'")))
			.log()
			.doOnError { throw InternalServerException("Erro ao resgatar ${Book.TABLE} com id '$id'") }
			.log()
			.flatMap {
				repo.save(
					Book(
						id,
						b.nmBook ?: it.nmBook!!,
						b.nmAuthor ?: it.nmAuthor!!,
						b.deDescription ?: it.deDescription!!,
						it.dtCreatedAt,
						it.dtUpdatedAt
					)
				)
			}
			.log()

	fun get(id: Int) = repo.findById(id)
		.doOnNext { if (it == null) throw NotFoundException("Não foi possível encontrar nenhum livro com id $id") }

	fun delete(id: Int) = repo.deleteById(id)

	fun list(pr: BookPagination): Flux<Book> = t.select(pr.toQuery(true), Book::class.java)

	fun count(pr: BookPagination): Mono<Long> = t.count(pr.toQuery(), Book::class.java)
}
