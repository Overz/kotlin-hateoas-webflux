package org.example.hateoas.errors

class InternalServerException(message: String, cause: Throwable?) : RuntimeException(message, cause) {

	constructor() : this("Internal Server Error", null)
	constructor(message: String) : this(message, null)
	constructor(cause: Throwable?) : this("Internal Server Error", cause)
}
