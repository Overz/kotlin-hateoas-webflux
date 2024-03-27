package org.example.hateoas.errors

import org.example.hateoas.errors.ErrorHandler.TemplateError.*
import org.springframework.http.*
import org.springframework.validation.FieldError
import org.springframework.validation.method.MethodValidationException
import org.springframework.web.ErrorResponse
import org.springframework.web.ErrorResponseException
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
class ErrorHandler : ResponseEntityExceptionHandler() {
	companion object {
		private const val DEFAULT_MSG = "Erro de validação"
		private const val TYPE = "https://developer.mozilla.org/en-US/docs/Web/HTTP/Status/"
	}

	private fun toView(
		t: TemplateError,
		ex: Exception,
		exchange: ServerWebExchange?,
		errors: Any?,
	): Mono<ResponseEntity<Any>> {
		val detail = (t.detail ?: if (ex is ErrorResponse) ex.body.detail else ex.message)!!
		val builder = ErrorResponse
			.builder(ex, t.status, detail)
			.title(t.title)
			.detailMessageCode(ErrorResponse.getDefaultDetailMessageCode(ex.javaClass, null))
			.typeMessageCode(ErrorResponse.getDefaultTypeMessageCode(ex.javaClass))
			.titleMessageCode(ErrorResponse.getDefaultTitleMessageCode(ex.javaClass))
			.type(URI.create(TYPE.plus(t.status.value())))
			.property("timestamp", LocalDateTime.now())

		var locale = Locale.getDefault()
		if (exchange != null) {
			locale = exchange.localeContext.locale
		}

		if (errors != null) {
			builder.property("errors", errors)
		}

		return Mono.just(
			ResponseEntity
				.status(t.status)
				.contentType(MediaType.APPLICATION_PROBLEM_JSON)
				.body(builder.build().updateAndGetBody(super.getMessageSource(), locale))
		)
	}

	private fun toView(
		t: TemplateError,
		ex: Exception,
		exchange: ServerWebExchange
	) = toView(t, ex, exchange, null)

	@ExceptionHandler(
		value = [
			Exception::class,
			InternalServerException::class
		]
	)
	fun handleException(ex: Exception): Mono<ResponseEntity<Any>> =
		toView(INTERNAL_ERROR_TEMPLATE, ex, null, null)

	override fun handleMethodNotAllowedException(
		ex: MethodNotAllowedException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		METHOD_NOT_ALLOWED_TEMPLATE,
		ex,
		exchange,
		mapOf("requested" to ex.httpMethod, "allowed" to ex.supportedMethods),
	)

	override fun handleNotAcceptableStatusException(
		ex: NotAcceptableStatusException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> {
		TODO("fazer")
	}

	override fun handleUnsupportedMediaTypeStatusException(
		ex: UnsupportedMediaTypeStatusException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		UNSUPPORTED_MEDIA_TYPE_TEMPLATE,
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
	): Mono<ResponseEntity<Any>> = toView(MISSING_REQUEST_VALUE_TEMPLATE, ex, exchange)

	override fun handleUnsatisfiedRequestParameterException(
		ex: UnsatisfiedRequestParameterException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		MISSING_REQUEST_PARAMETER_TEMPLATE,
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
		MISSING_REQUEST_VALUE_TEMPLATE,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapOf("field" to it.field, "message" to it.defaultMessage)
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
		MISSING_REQUEST_VALUE_TEMPLATE,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapOf("field" to it.field, "message" to it.defaultMessage)
				else -> it.defaultMessage ?: DEFAULT_MSG
			}
		}
	)

	override fun handleServerWebInputException(
		ex: ServerWebInputException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(BAD_REQUEST_TEMPLATE, ex, exchange)

	override fun handleServerErrorException(
		ex: ServerErrorException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> {
		TODO("fazer")
	}

	override fun handleResponseStatusException(
		ex: ResponseStatusException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> {
		TODO("fazer")
	}

	override fun handleErrorResponseException(
		ex: ErrorResponseException,
		headers: HttpHeaders,
		status: HttpStatusCode,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> {
		TODO("fazer")
	}

	override fun handleMethodValidationException(
		ex: MethodValidationException,
		status: HttpStatus,
		exchange: ServerWebExchange
	): Mono<ResponseEntity<Any>> = toView(
		BAD_REQUEST_TEMPLATE,
		ex,
		exchange,
		ex.allErrors.map {
			when (it) {
				is FieldError -> mapOf("field" to it.field, "message" to it.defaultMessage)
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
	): Mono<ResponseEntity<Any>> = toView(INTERNAL_ERROR_TEMPLATE, ex, exchange)

	enum class TemplateError(
		val title: String,
		val detail: String?,
		val status: HttpStatus
	) {
		BAD_REQUEST_TEMPLATE("Erro do Cliente", null, HttpStatus.BAD_REQUEST),
		MISSING_REQUEST_VALUE_TEMPLATE(
			"Violação de Constraint",
			"Valores obrigatórios não foram informados",
			HttpStatus.BAD_REQUEST
		),
		HTTP_MESSAGE_NOT_READABLE_TEMPLATE(
			"Requisição Não Processada",
			"Não foi possível ler o conteúdo da requisição. Possível má formatação?",
			HttpStatus.BAD_REQUEST
		),
		METHOD_ARGUMENT_NOT_VALID_TEMPLATE("Erro na Validação", null, HttpStatus.BAD_REQUEST),
		MISSING_REQUEST_PARAMETER_TEMPLATE(
			"Parâmetro de Solicitação Ausente",
			null,
			HttpStatus.BAD_REQUEST
		),
		MISSING_PATH_VARIABLE_TEMPLATE("Variável de Caminho Ausente", null, HttpStatus.BAD_REQUEST),
		FORBIDDEN_TEMPLATE("Acesso Negado", "Permissões Insuficientes", HttpStatus.FORBIDDEN),
		NOT_FOUND_TEMPLATE("Recurso Não Encontrado", null, HttpStatus.NOT_FOUND),
		METHOD_NOT_ALLOWED_TEMPLATE(
			"Método de Requisição Não Suportado",
			null,
			HttpStatus.METHOD_NOT_ALLOWED
		),
		NO_HANDLER_FOUND_TEMPLATE(
			"Manipulador Não Encontrado",
			"O endpoint solicitado não possui formas de processar a requisição",
			HttpStatus.NOT_FOUND
		),
		UNSUPPORTED_MEDIA_TYPE_TEMPLATE(
			"Mídia Não Suportado",
			null,
			HttpStatus.UNSUPPORTED_MEDIA_TYPE
		),
		INTERNAL_ERROR_TEMPLATE(
			"Erro Interno do Servidor",
			"Ocorreu um erro interno ao processar a solicitação.",
			HttpStatus.INTERNAL_SERVER_ERROR
		),
		HTTP_MESSAGE_NOT_WRITABLE_TEMPLATE(
			"Erro na Escrita da Mensagem",
			"Ocorreu um erro interno ao processar a solicitação",
			HttpStatus.INTERNAL_SERVER_ERROR
		);
	}
}