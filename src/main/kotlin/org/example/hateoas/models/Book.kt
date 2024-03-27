package org.example.hateoas.models

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import java.time.LocalDateTime

@Table("PUBLIC.book")
data class Book(
	@Id
	@Column("cdBook")
	var cdBook: Int?,

	@Column("nmBook")
	var nmBook: String,

	@Column("nmAuthor")
	var nmAuthor: String,

	@Column("deDescription")
	var deDescription: String,

	@Column("dtCreatedAt")
	var dtCreatedAt: LocalDateTime?,

	@Column("dtUpdatedAt")
	var dtUpdatedAt: LocalDateTime?,
) : Serializable {

	fun convert(): BookDTO = BookDTO(cdBook, nmBook, nmAuthor, deDescription, dtCreatedAt, dtUpdatedAt)
}

data class BookDTO(
	@field:Null
	var id: Int?,
	@field:NotEmpty(message = "nao pode ser vazio/nulo")
	var name: String?,
	@field:NotEmpty(message = "nao pode ser vazio/nulo")
	var author: String?,
	@field:NotEmpty(message = "nao pode ser vazio/nulo")
	var description: String?,
	@field:Null
	var createdAt: LocalDateTime?,
	@field:Null
	var updatedAt: LocalDateTime?,
) : Serializable {

	fun convert(): Book = Book(id, name!!, author!!, description!!, createdAt, updatedAt)
}
