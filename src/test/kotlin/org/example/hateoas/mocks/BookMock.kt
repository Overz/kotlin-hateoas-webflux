package org.example.hateoas.mocks

import org.apache.commons.lang3.RandomStringUtils
import org.example.hateoas.Book
import org.example.hateoas.generators.RandomNumberUtils
import java.time.LocalDate

object BookMock {
	private var randomNumberUtils = RandomNumberUtils()

	private val id = randomNumberUtils.nextInt()
	private val name = RandomStringUtils.random(10)
	private val author = RandomStringUtils.random(10)
	private val description = RandomStringUtils.random(10)
	private val date1 = LocalDate.now()
	private val date2 = LocalDate.now()

	fun book(b: Book? = null): Book {
		val data = Book(id, name, author, description, date1, date2)

		if (b == null) return data

		data.cdBook = b.cdBook ?: data.cdBook
		data.nmBook = b.nmBook ?: data.nmBook
		data.nmAuthor = b.nmAuthor ?: data.nmAuthor
		data.deDescription = b.deDescription ?: data.deDescription
		data.dtCreatedAt = b.dtCreatedAt ?: data.dtCreatedAt
		data.dtUpdatedAt = b.dtUpdatedAt ?: data.dtUpdatedAt

		return data
	}

	fun nullBook(): Book = Book(null, null, null, null, null, null)
}
