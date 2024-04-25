package org.example.hateoas.utils

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.*
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.hateoas.Link
import java.io.Serializable


abstract class PaginationRequest(
	@field:PositiveOrZero(message = "parâmetro precisa ser igual ou maior que zero")
	var page: Int = PAGE_VALUE,

	@field:Max(100)
	@field:PositiveOrZero(message = "parâmetro precisa ser igual ou maior que zero")
	var pageSize: Int = PAGE_SIZE_VALUE,

	@field:Pattern(
		message = "deve ser 'asc' ou 'desc' obrigatoriamente",
		regexp = "^\\w+:(asc|desc)(,\\w+:(asc|desc))*\$",
		flags = [Pattern.Flag.CASE_INSENSITIVE]
	)
	var sortBy: String? = DEFAULT_SORT_VALUE,
) : Serializable {
	companion object {
		const val PAGE_VALUE = 0
		const val PAGE_SIZE_VALUE = 10
		const val DEFAULT_SORT_VALUE = "id:asc"
	}

	/**
	 * jsonName:dbColumnName
	 */
	abstract fun getMappedFields(): Map<String, String>
	abstract fun buildCriteria(): Criteria
	abstract fun toQuery(withPagination: Boolean? = false): Query

	fun toPageRequest() = PageRequest.of(page, pageSize, getSorters())

	private fun getSorters(): Sort {
		val fields = this.getMappedFields()
		val orders = mutableListOf<Order>()

		// field1:asc,field2:desc,...
		for (p in this.sortBy!!.split(",")) {
			val (name, arg) = p.split(":")
			if (fields.containsKey(name)) {
				val direction = Direction.valueOf(arg.uppercase())
				val order = Order(direction, fields.getValue(name))
				orders.add(order)
			}
		}

		return if (orders.isNotEmpty()) by(orders) else unsorted()
	}
}

data class PaginationResult<T : Serializable>(
	var page: Int?,
	var pageSize: Int?,
	var totalRecords: Long?,
	var data: List<T>?,
	var links: List<Link>?
)
