package org.example.hateoas

import org.example.hateoas.mocks.BookMock
import org.example.hateoas.mocks.R2dbcEntityTemplateMock
import org.example.hateoas.mocks.RepositoryMock
import org.example.hateoas.mocks.ServiceMock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import reactor.core.publisher.Mono

class ControllerTest : Assertions() {

	val repo = RepositoryMock()
	val template = R2dbcEntityTemplateMock()
	val service = ServiceMock(repo, template)
	val ctrl = Controller(service)

	@Test
	@DisplayName("")
	fun test() {
		Mockito.`when`(ctrl.post(Mockito.any(Book::class.java)))
			.thenReturn(Mono.just(BookMock.book()))
	}
}
