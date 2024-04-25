package org.example.hateoas.mocks

import io.r2dbc.spi.ConnectionFactory
import org.mockito.Mockito.mock
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

class R2dbcEntityTemplateMock : R2dbcEntityTemplate(mock(ConnectionFactory::class.java))
