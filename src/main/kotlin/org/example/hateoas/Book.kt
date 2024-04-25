package org.example.hateoas

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Null
import org.example.hateoas.utils.PaginationRequest
import org.example.hateoas.utils.ValidationMessages.VAZIO_NULO
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.format.annotation.DateTimeFormat
import java.io.Serializable
import java.time.LocalDate

@Table(Book.TABLE)
data class Book(
	@Id
	@Column(CD_BOOK)
	@JsonProperty(JSON_CD_BOOK)
	@field:Null
	var cdBook: Int?,

	@Column(NM_BOOK)
	@JsonProperty(JSON_NM_BOOK)
	@field:NotEmpty(message = VAZIO_NULO)
	var nmBook: String?,

	@Column(NM_AUTHOR)
	@JsonProperty(JSON_NM_AUTHOR)
	@field:NotEmpty(message = VAZIO_NULO)
	var nmAuthor: String?,

	@Column(DE_DESCRIPTION)
	@JsonProperty(JSON_DE_DESCRIPTION)
	@field:NotEmpty(message = VAZIO_NULO)
	var deDescription: String?,

	@Column(DT_CREATED_AT)
	@JsonProperty(JSON_DT_CREATED_AT)
	@field:Null
	@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	var dtCreatedAt: LocalDate?,

	@Column(DT_UPDATED_AT)
	@JsonProperty(JSON_DT_UPDATED_AT)
	@field:Null
	@field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	var dtUpdatedAt: LocalDate?,
) : Serializable {

	companion object {
		const val TABLE = "book"
		const val CD_BOOK = "cdBook"
		const val JSON_CD_BOOK = "id"
		const val NM_BOOK = "nmBook"
		const val JSON_NM_BOOK = "name"
		const val NM_AUTHOR = "nmAuthor"
		const val JSON_NM_AUTHOR = "author"
		const val DE_DESCRIPTION = "deDescription"
		const val JSON_DE_DESCRIPTION = "description"
		const val DT_CREATED_AT = "dtCreatedAt"
		const val JSON_DT_CREATED_AT = "created_at"
		const val DT_UPDATED_AT = "dtUpdatedAt"
		const val JSON_DT_UPDATED_AT = "updated_at"
	}
}

data class BookPagination(
	var name: String?,
	var author: String?,
	var description: String?,
	var created_at_start: LocalDate?,
	var created_at_end: LocalDate?,
) : PaginationRequest(PAGE_VALUE, PAGE_SIZE_VALUE, DEFAULT_SORT_VALUE) {
	override fun getMappedFields(): Map<String, String> =
		mapOf(
			Book.JSON_CD_BOOK to Book.CD_BOOK,
			Book.JSON_NM_BOOK to Book.NM_BOOK,
			Book.JSON_NM_AUTHOR to Book.NM_AUTHOR,
			Book.JSON_DE_DESCRIPTION to Book.DE_DESCRIPTION,
			Book.JSON_DT_CREATED_AT to Book.DT_CREATED_AT,
			Book.JSON_DT_UPDATED_AT to Book.DT_UPDATED_AT,
		)

	override fun buildCriteria(): Criteria = Criteria.from(
		listOfNotNull(
			name?.let { Criteria.where(Book.NM_BOOK).like("%$it%") },
			author?.let { Criteria.where(Book.NM_AUTHOR).like("%$it%") },
			description?.let { Criteria.where(Book.DE_DESCRIPTION).like("%$it%") },
			created_at_start?.let { Criteria.where(Book.DT_CREATED_AT).greaterThanOrEquals(it) },
			created_at_end?.let { Criteria.where(Book.DT_CREATED_AT).lessThanOrEquals(it) }
		)
	)

	override fun toQuery(withPagination: Boolean?): Query =
		if (withPagination == true) Query.query(buildCriteria()).with(toPageRequest())
		else Query.query(buildCriteria())
}
