package org.example.hateoas.domain

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import org.example.hateoas.utils.PaginationRequest
import org.example.hateoas.utils.ValidationMessages.VAZIO_NULO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.relational.core.query.Criteria
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDate

@Table(Book.TABLE)
data class Book(
	@Id
	@Column(CD_BOOK)
	var cdBook: Int?,

	@Column(NM_BOOK)
	var nmBook: String?,

	@Column(NM_AUTHOR)
	var nmAuthor: String?,

	@Column(DE_DESCRIPTION)
	var deDescription: String?,

	@Column(DT_CREATED_AT)
	var dtCreatedAt: LocalDate?,

	@Column(DT_UPDATED_AT)
	var dtUpdatedAt: LocalDate?,
) : Serializable {

	companion object {
		const val TABLE = "book"
		const val CD_BOOK = "cdBook"
		const val NM_BOOK = "nmBook"
		const val NM_AUTHOR = "nmAuthor"
		const val DE_DESCRIPTION = "deDescription"
		const val DT_CREATED_AT = "dtCreatedAt"
		const val DT_UPDATED_AT = "dtUpdatedAt"
	}

	open class Builder {
		private val b: Book = Book(null, null, null, null, null, null)
		fun cdBook(cdBook: Int) = apply { this.b.cdBook = cdBook }
		fun nmBook(nmBook: String) = apply { b.nmBook = nmBook }
		fun nmAuthor(nmAuthor: String) = apply { b.nmAuthor = nmAuthor }
		fun deDescription(deDescription: String) = apply { b.deDescription = deDescription }
		fun dtCreatedAt(dtCreatedAt: LocalDate) = apply { b.dtCreatedAt = dtCreatedAt }
		fun dtUpdatedAt(dtUpdatedAt: LocalDate) = apply { b.dtUpdatedAt = dtUpdatedAt }
		fun build() = b
	}

	fun toDto(): BookDTO = BookDTO(cdBook, nmBook, nmAuthor, deDescription, dtCreatedAt, dtUpdatedAt)
}

data class BookDTO(
	@field:Null
	var id: Int?,
	@field:NotEmpty(message = VAZIO_NULO)
	var name: String?,
	@field:NotEmpty(message = VAZIO_NULO)
	var author: String?,
	@field:NotEmpty(message = VAZIO_NULO)
	var description: String?,
	@field:Null
	@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	var createdAt: LocalDate?,
	@field:Null
	@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	var updatedAt: LocalDate?,
) : Serializable {
	companion object {
		const val TABLE = "book"
		const val ID = "id"
		const val NAME = "name"
		const val AUTHOR = "author"
		const val DESCRIPTION = "description"
		const val CREATED_AT = "createdAt"
		const val UPDATED_AT = "updatedAt"
	}

	open class Builder {
		private val b: BookDTO = BookDTO(null, null, null, null, null, null)
		fun id(id: Int) = apply { this.b.id = id }
		fun name(name: String) = apply { b.name = name }
		fun author(author: String) = apply { b.author = author }
		fun description(description: String) = apply { b.description = description }
		fun createdAt(createdAt: LocalDate) = apply { b.createdAt = createdAt }
		fun updatedAt(updatedAt: LocalDate) = apply { b.updatedAt = updatedAt }
		fun build() = b
	}

	fun toEntity(): Book = Book(id, name, author, description, createdAt, updatedAt)
}

data class BookPagination(
	var name: String?,
	var author: String?,
	var description: String?,
	var createdAtStart: LocalDate?,
	var createdAtEnd: LocalDate?,
) : PaginationRequest() {
	override fun fields(): Map<String, String> =
		mapOf(
			BookDTO.ID to Book.CD_BOOK,
			BookDTO.NAME to Book.NM_BOOK,
			BookDTO.AUTHOR to Book.NM_AUTHOR,
			BookDTO.DESCRIPTION to Book.DE_DESCRIPTION,
			BookDTO.CREATED_AT to Book.DT_CREATED_AT,
			BookDTO.UPDATED_AT to Book.DT_UPDATED_AT,
		)

	override fun criteria(): Criteria = Criteria.from(
		listOfNotNull(
			name?.let { Criteria.where(Book.NM_BOOK).like("%$it%") },
			author?.let { Criteria.where(Book.NM_AUTHOR).like("%$it%") },
			description?.let { Criteria.where(Book.DE_DESCRIPTION).like("%$it%") },
			createdAtStart?.let { Criteria.where(Book.DT_CREATED_AT).greaterThanOrEquals(it) },
			createdAtEnd?.let { Criteria.where(Book.DT_CREATED_AT).lessThanOrEquals(it) }
		)
	)
}
