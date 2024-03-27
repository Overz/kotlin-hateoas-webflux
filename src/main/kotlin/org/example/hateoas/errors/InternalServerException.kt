package org.example.hateoas.errors

class InternalServerException(cause: Throwable?) : RuntimeException("Internal Server Error", cause)
