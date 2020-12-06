package ktor.simple.rest.service.flat.dao

import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.utils.FlatsGenerator

object FlatsRepository {

    private val generator = FlatsGenerator()
    private val data by lazy {
        generator.generateFlats(3)
            .map { it.id to it }
            .toMap()
    }

    fun findAll(): Map<Int, Flat> = data

    fun findOne(id: Int): Flat? = data[id]

}