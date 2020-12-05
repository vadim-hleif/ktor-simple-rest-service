package ktor.simple.rest.service

class App {
    val greeting: String
        get() = "Hello world."
}

fun main() = println(App().greeting)