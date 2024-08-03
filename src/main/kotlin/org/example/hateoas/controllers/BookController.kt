package org.example.hateoas.controllers

import jakarta.validation.Valid
import org.example.hateoas.domain.BookDTO
import org.example.hateoas.domain.BookPagination
import org.example.hateoas.services.BookService
import org.example.hateoas.utils.CustomMediaType.APPLICATION_YAML_VALUE
import org.example.hateoas.utils.PaginationResult
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.APPLICATION_XML_VALUE
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/book")
class BookController(private val service: BookService) {

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
		consumes = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun post(@RequestBody @Valid b: BookDTO): Mono<BookDTO> =
		service.create(b.toEntity()).flatMap { Mono.just(it.toDto()) }

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping(
		path = ["/{id}"],
	)
	fun put(@PathVariable("id") id: Int, @RequestBody @Valid b: BookDTO): Mono<Void> =
		service.update(id, b.toEntity()).then()

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping(
		path = ["/{id}"],
		consumes = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun patch(@PathVariable("id") id: Int, @RequestBody b: BookDTO): Mono<Void> = service.update(id, b.toEntity()).then()

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		path = ["/{id}"],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun get(@PathVariable("id") id: Int): Mono<BookDTO> = service.get(id).flatMap { Mono.just(it.toDto()) }

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(
		path = ["/{id}"],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun delete(@PathVariable("id") id: Int): Mono<Void> = service.delete(id)

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun get(@ModelAttribute @Valid pr: BookPagination): Mono<PaginationResult<BookDTO>> =
		service.list(pr)
			.collectList()
			.zipWith(service.count(pr))
			.map { zip ->
				val data = zip.t1.map { b -> linkTo(methodOn(BookController::class.java).get(b.cdBook!!)).withSelfRel() }
				Triple(zip.t1.map { it.toDto() }, zip.t2, data)
			}
			.flatMap { Mono.just(PaginationResult(pr.page, pr.pageSize, it.second, it.first, it.third)) }
}
