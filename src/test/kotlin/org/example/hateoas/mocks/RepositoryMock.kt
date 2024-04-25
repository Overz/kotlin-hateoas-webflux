package org.example.hateoas.mocks

import org.example.hateoas.Book
import org.example.hateoas.Repository
import org.reactivestreams.Publisher
import org.springframework.data.domain.Example
import org.springframework.data.domain.Sort
import org.springframework.data.repository.query.FluentQuery
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function

class RepositoryMock : Repository {
	override fun <S : Book?> save(entity: S & Any): Mono<S> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> saveAll(entities: MutableIterable<S>): Flux<S> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> saveAll(entityStream: Publisher<S>): Flux<S> {
		TODO("Not yet implemented")
	}

	override fun findById(id: Publisher<Int>): Mono<Book> {
		TODO("Not yet implemented")
	}

	override fun existsById(id: Publisher<Int>): Mono<Boolean> {
		TODO("Not yet implemented")
	}

	override fun findAll(): Flux<Book> {
		TODO("Not yet implemented")
	}

	override fun findAll(sort: Sort): Flux<Book> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> findAll(example: Example<S>): Flux<S> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> findAll(example: Example<S>, sort: Sort): Flux<S> {
		TODO("Not yet implemented")
	}

	override fun findAllById(ids: MutableIterable<Int>): Flux<Book> {
		TODO("Not yet implemented")
	}

	override fun findAllById(idStream: Publisher<Int>): Flux<Book> {
		TODO("Not yet implemented")
	}

	override fun count(): Mono<Long> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> count(example: Example<S>): Mono<Long> {
		TODO("Not yet implemented")
	}

	override fun deleteById(id: Publisher<Int>): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun delete(entity: Book): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun deleteAllById(ids: MutableIterable<Int>): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun deleteAll(entities: MutableIterable<Book>): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun deleteAll(entityStream: Publisher<out Book>): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun deleteAll(): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> findOne(example: Example<S>): Mono<S> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?> exists(example: Example<S>): Mono<Boolean> {
		TODO("Not yet implemented")
	}

	override fun <S : Book?, R : Any?, P : Publisher<R>?> findBy(
		example: Example<S>,
		queryFunction: Function<FluentQuery.ReactiveFluentQuery<S>, P>
	): P & Any {
		TODO("Not yet implemented")
	}

	override fun deleteById(id: Int): Mono<Void> {
		TODO("Not yet implemented")
	}

	override fun existsById(id: Int): Mono<Boolean> {
		TODO("Not yet implemented")
	}

	override fun findById(id: Int): Mono<Book> {
		TODO("Not yet implemented")
	}
}
