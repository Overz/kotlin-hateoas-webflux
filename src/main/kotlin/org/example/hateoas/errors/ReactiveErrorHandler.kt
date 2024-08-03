package org.example.hateoas.errors

import org.example.hateoas.utils.MapperUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.*
import reactor.core.publisher.Mono
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@RestControllerAdvice
class ReactiveErrorHandler : ResponseEntityExceptionHandler() {
	companion object {
		private val log = LoggerFactory.getLogger(ReactiveErrorHandler::class.java.simpleName)
		private const val DEFAULT_MSG = "Validation error"
		private val TYPE = URI.create("https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/")
	}

	private fun responseEntity(
		t: TemplateError,
		ex: Exception,
		exchange: ServerWebExchange?,
		errors: Any?
	): ResponseEntity<Any> {
		log.error("Mapping error, template: '{}'", t, ex)
		val detail = (t.detail ?: if (ex is ErrorResponse) ex.body.detail else ex.message)!!
		val builder = ErrorResponse
			.builder(ex, t.status, detail)
			.title(t.title)
			.detailMessageCode(ErrorResponse.getDefaultDetailMessageCode(ex.javaClass, null))
			.typeMessageCode(ErrorResponse.getDefaultTypeMessageCode(ex.javaClass))
			.titleMessageCode(ErrorResponse.getDefaultTitleMessageCode(ex.javaClass))
			.type(TYPE.resolve("" + t.status.value()))
			.property("timestamp", LocalDateTime.now())

		if (errors != null) {
			builder.property("errors", errors)
		}

		var locale = Locale.getDefault()
		if (exchange != null) {
			locale = exchange.localeContext.locale
			builder.instance(URI.create(exchange.request.path.value()))
		}

		val body = builder.build().updateAndGetBody(messageSource, locale)
		return ResponseEntity.status(t.status).contentType(APPLICATION_PROBLEM_JSON).body(body)
	}

	private fun toView(
		t: TemplateError,
		ex: Exception,
		exchange: ServerWebExchange?,
		errors: Any?,
	): Mono<ResponseEntity<Any>> = Mono.just(responseEntity(t, ex, exchange, errors))

	private fun toView(
		t: TemplateError,
		ex: Exception,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(t, ex, exchange, null)

	private fun mapFieldError(f: FieldError, cls: Class<*>? = null): Map<String, Any?> {
		var name: String? = f.field
		if (cls != null) {
			val annotations = MapperUtils.getAnnotation(cls)
			name = annotations.find { it.field.name.lowercase() == name!!.lowercase() }?.name
		}

		return mapOf(
			"field" to name,
			"message" to f.defaultMessage,
			"value" to f.rejectedValue
		)
	}

	@ExceptionHandler(
		Exception::class,
		InternalServerException::class
	)
	fun handleCustomException(ex: Exception): ResponseEntity<Any> {
		return responseEntity(TemplateError.INTERNAL_ERROR, ex, null, null)
	}

	@ExceptionHandler(NotFoundException::class)
	fun handleNotFoundException(ex: NotFoundException):ResponseEntity<Any> {
		return responseEntity(TemplateError.NOT_FOUND, ex, null, null)
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<Any> =
		responseEntity(
			TemplateError.METHOD_ARGUMENT_NOT_VALID,
			ex,
			null,
			ex.allErrors.map {
				when (it) {
					is FieldError -> mapFieldError(it, ex.parameter.parameterType)
					else -> it.defaultMessage ?: DEFAULT_MSG
				}
			})

	override fun handleMethodNotAllowedException(
		ex: MethodNotAllowedException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.METHOD_NOT_ALLOWED,
		ex,
		exchange,
		mapOf("requested" to ex.httpMethod, "allowed" to ex.supportedMethods),
	)

	override fun handleUnsupportedMediaTypeStatusException(
		ex: UnsupportedMediaTypeStatusException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.UNSUPPORTED_MEDIA_TYPE,
		ex,
		exchange,
		mapOf(
			"accepted" to ex.supportedMediaTypes,
			"informed" to ex.contentType
		)
	)

	override fun handleMissingRequestValueException(
		ex: MissingRequestValueException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(TemplateError.MISSING_REQUEST_VALUE, ex, exchange)

	override fun handleUnsatisfiedRequestParameterException(
		ex: UnsatisfiedRequestParameterException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.MISSING_REQUEST_PARAMETER,
		ex,
		exchange,
		mapOf(
			"field" to ex.methodParameter?.parameterName,
			"type" to ex.methodParameter?.parameterType?.simpleName
		)
	)

	override fun handleWebExchangeBindException(
		ex: WebExchangeBindException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.MISSING_REQUEST_VALUE,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapFieldError(it)
				else -> it.defaultMessage ?: DEFAULT_MSG
			}
		}
	)

	override fun handleHandlerMethodValidationException(
		ex: HandlerMethodValidationException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.MISSING_REQUEST_VALUE,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapFieldError(it)
				else -> it.defaultMessage ?: DEFAULT_MSG
			}
		}
	)

	override fun handleServerWebInputException(
		ex: ServerWebInputException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(TemplateError.BAD_REQUEST, ex, exchange)

	override fun handleMethodValidationException(
		ex: MethodValidationException,
		status: HttpStatus,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		TemplateError.BAD_REQUEST,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapFieldError(it)
				else -> it.defaultMessage ?: DEFAULT_MSG
			}
		}
	)

	override fun handleExceptionInternal(
		ex: Exception,
		body: Any?,
		headers: HttpHeaders?,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(TemplateError.INTERNAL_ERROR, ex, exchange)

	enum class TemplateError(
		val title: String,
		val detail: String?,
		val status: HttpStatus
	) {
		BAD_REQUEST("Customer error", null, HttpStatus.BAD_REQUEST),
		MISSING_REQUEST_VALUE(
			"Constraint Violation",
			"Mandatory values were not informed",
			HttpStatus.BAD_REQUEST
		),
		HTTP_MESSAGE_NOT_READABLE(
			"Unprocessed Request",
			"Unable to read the content of the request. Possible bad formatting?",
			HttpStatus.BAD_REQUEST
		),
		METHOD_ARGUMENT_NOT_VALID("Validation Error", null, HttpStatus.BAD_REQUEST),
		MISSING_REQUEST_PARAMETER(
			"Missing Request Parameter",
			null,
			HttpStatus.BAD_REQUEST
		),
		MISSING_PATH_VARIABLE("Missing Path Variable", null, HttpStatus.BAD_REQUEST),
		FORBIDDEN("Access Denied", "Insufficient Permissions.", HttpStatus.FORBIDDEN),
		NOT_FOUND("Resource Not Found", null, HttpStatus.NOT_FOUND),
		METHOD_NOT_ALLOWED(
			"Unsupported Request Method",
			null,
			HttpStatus.METHOD_NOT_ALLOWED
		),
		NO_HANDLER_FOUND(
			"Handler Not Found",
			"The requested endpoint has no way to process the request.",
			HttpStatus.NOT_FOUND
		),
		UNSUPPORTED_MEDIA_TYPE(
			"Unsupported Media-Type",
			null,
			HttpStatus.UNSUPPORTED_MEDIA_TYPE
		),
		INTERNAL_ERROR(
			"Internal Server Error",
			"An internal error occurred while processing the request.",
			HttpStatus.INTERNAL_SERVER_ERROR
		),
		HTTP_MESSAGE_NOT_WRITABLE(
			"Error writing message",
			"An internal error occurred while processing the request.",
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}
