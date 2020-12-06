package ktor.simple.rest.service.flat.utils

import java.util.concurrent.atomic.AtomicInteger

/**
 * Is used for unique ids in scope of one app run
 */
object IdGenerator {
    private val index = AtomicInteger()

    fun genNextId(): Int = index.incrementAndGet()
}