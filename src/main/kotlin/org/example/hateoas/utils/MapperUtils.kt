package org.example.hateoas.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object MapperUtils {
	private val mapper = ObjectMapper()

	init {
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
	}

	fun toJson(o: Any): String = synchronized(mapper) { mapper.writeValueAsString(o) }
	fun <T> fromJson(o: String, cls: Class<T>): T = synchronized(mapper) { mapper.readValue(o, cls) }
	fun <T> cast(o: Any, cls: Class<T>): T = synchronized(mapper) { mapper.convertValue(o, cls) }
	fun toMap(o: Any): Map<*, *> = synchronized(mapper) { mapper.readValue(toJson(o), Map::class.java) }
}
