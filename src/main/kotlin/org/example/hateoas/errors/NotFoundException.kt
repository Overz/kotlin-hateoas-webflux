package org.example.hateoas.errors

class NotFoundException(message: String, cause: Throwable?) : RuntimeException(message, cause) {
	constructor(cause: Throwable?) : this("Not Found", cause)
	constructor(message: String) : this(message, null)
}
