package org.example.hateoas

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Main

fun main(args: Array<String>) {
	Dotenv
		.configure()
		.systemProperties()
		.ignoreIfMalformed()
		.load()

	runApplication<Main>(*args)
}
