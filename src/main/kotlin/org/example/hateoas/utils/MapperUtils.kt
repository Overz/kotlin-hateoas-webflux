package org.example.hateoas.utils

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule

object MapperUtils {
	private val mapper = ObjectMapper()

	init {
		mapper
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
			.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
			.registerModules(JavaTimeModule())
			.registerModules(ParameterNamesModule())
			.registerModules(Jdk8Module())
	}

	fun toJson(o: Any): String = synchronized(mapper) { mapper.writeValueAsString(o) }
	fun <T> fromJson(o: String, cls: Class<T>): T = synchronized(mapper) { mapper.readValue(o, cls) }
	fun <T> cast(o: Any, cls: Class<T>): T = synchronized(mapper) { mapper.convertValue(o, cls) }
	fun toMap(o: Any): Map<*, *> = synchronized(mapper) { mapper.readValue(toJson(o), Map::class.java) }

	fun getAnnotation(cls: Class<*>): MutableList<BeanPropertyDefinition> = synchronized(mapper) {
		val type = jacksonObjectMapper().constructType(cls)
		mapper.serializationConfig.introspect(type).findProperties()
	}
}
