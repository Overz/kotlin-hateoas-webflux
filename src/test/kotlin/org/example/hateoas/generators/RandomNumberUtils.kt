package org.example.hateoas.generators

import org.apache.commons.rng.core.source64.LongProvider

class RandomNumberUtils : LongProvider() {
	override fun next(): Long = 0L
}
