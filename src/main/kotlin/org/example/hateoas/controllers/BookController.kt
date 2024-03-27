package org.example.hateoas.controllers

import jakarta.validation.Valid
import org.example.hateoas.models.BookDTO
import org.example.hateoas.services.BookService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/book")
class BookController(private val bookService: BookService) {

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
		consumes = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE],
		produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE]
	)
	fun create(@RequestBody @Valid body: Mono<BookDTO>): Mono<BookDTO> = body.flatMap { dto ->
		bookService.create(dto.convert()).map { it.convert() }
	}

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE]
	)
	fun list(): Flux<BookDTO> = bookService.list().map { it.convert() }
}
