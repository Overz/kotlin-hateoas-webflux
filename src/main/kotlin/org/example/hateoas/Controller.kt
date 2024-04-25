package org.example.hateoas

import jakarta.validation.Valid
import org.example.hateoas.errors.InternalServerException
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
class Controller(private val service: Service) {

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
		consumes = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun post(@RequestBody @Valid b: Book) = service.create(b)

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping(
		path = ["/{id}"],
	)
	fun put(@PathVariable("id") id: Int, @RequestBody @Valid b: Book) = service.update(id, b).then()

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping(
		path = ["/{id}"],
		consumes = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun patch(@PathVariable("id") id: Int, @RequestBody b: Book) = service.update(id, b).then()

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		path = ["/{id}"],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun get(@PathVariable("id") id: Int) = service.get(id)

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(
		path = ["/{id}"],
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun delete(@PathVariable("id") id: Int) = service.delete(id)

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		produces = [APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE, APPLICATION_YAML_VALUE]
	)
	fun get(@ModelAttribute @Valid pr: BookPagination): Mono<PaginationResult<Book>> =
		service.list(pr)
			.collectList()
			.zipWith(service.count(pr))
			.map {
				val data = it.t1.map { b -> linkTo(methodOn(Controller::class.java).get(b.cdBook!!)).withSelfRel() }
				Triple(it.t1, it.t2, data)
			}
			.flatMap { Mono.just(PaginationResult(pr.page, pr.pageSize, it.second, it.first, it.third)) }
}
