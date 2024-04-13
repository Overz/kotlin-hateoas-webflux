package org.example.hateoas.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.example.hateoas.utils.CustomMediaType
import org.springframework.http.MediaType
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter
import org.springframework.stereotype.Component

@Component
class YamlHttpMessageConverterConfig : AbstractJackson2HttpMessageConverter(
	YAML_MAPPER,
	MediaType.parseMediaType(CustomMediaType.APPLICATION_YAML_VALUE)
) {
	companion object {
		private val YAML_MAPPER: ObjectMapper = YAMLMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			.registerModules(JavaTimeModule())
	}
}
