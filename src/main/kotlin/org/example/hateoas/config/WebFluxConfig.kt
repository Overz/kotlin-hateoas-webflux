package org.example.hateoas.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class WebFluxConfig(
	private val yamlConverter: YamlHttpMessageConverterConfig
) : WebFluxConfigurer {

	override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
		configurer.customCodecs().registerWithDefaultConfig(yamlConverter)
	}
}
