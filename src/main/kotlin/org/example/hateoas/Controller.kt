package org.example.hateoas

import jakarta.validation.Valid
import org.example.hateoas.utils.CustomMediaType
import org.example.hateoas.utils.PaginationResult
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.hateoas.server.reactive.ReactiveRepresentationModelAssembler
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Validated
@RestController
@RequestMapping("/book")
class Controller(private val service: Service) {

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping(
		consumes = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		],
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
	)
	fun post(@RequestBody @Valid body: Mono<Book>) = body.flatMap { dto -> service.create(dto) }

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PutMapping(
		consumes = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,

			CustomMediaType.APPLICATION_YAML_VALUE
		],
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
	)
	fun put(@RequestBody @Valid body: Mono<Book>) = body.flatMap { service.update(it) }.then()

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PatchMapping(
		consumes = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		],
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
	)
	fun patch(@RequestBody body: Mono<Book>) = body.flatMap { service.update(it) }.then()

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		path = ["/{id}"],
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
	)
	fun get(@PathVariable("id") id: Int) = service.get(id).map { it }

	@ResponseStatus(HttpStatus.OK)
	@DeleteMapping(
		path = ["/{id}"],
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
	)
	fun delete(@PathVariable("id") id: Int) = service.delete(id)

	@ResponseStatus(HttpStatus.OK)
	@GetMapping(
		produces = [
			MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE,
			CustomMediaType.APPLICATION_YAML_VALUE
		]
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

class Teste : ReactiveRepresentationModelAssembler<Book, EntityModel<Book>> {
	override fun toModel(entity: Book, exchange: ServerWebExchange): Mono<EntityModel<Book>> = Mono.just(
		EntityModel.of(
			entity,
			linkTo(methodOn(Controller::class.java).get(entity.cdBook!!)).withSelfRel()
		)
	)

}
