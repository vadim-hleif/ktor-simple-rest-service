package ktor.simple.rest.service.flat.utils

import java.util.concurrent.atomic.AtomicInteger

object IdGenerator {
    private val index = AtomicInteger()

    fun genNextId(): Int = index.incrementAndGet()
}