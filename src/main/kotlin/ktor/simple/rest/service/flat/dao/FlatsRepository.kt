package ktor.simple.rest.service.flat.dao

import ktor.simple.rest.service.flat.dtos.Flat
import ktor.simple.rest.service.flat.utils.FlatsGenerator

object FlatsRepository {

    private val generator = FlatsGenerator()
    private val data by lazy {
        (1..3).map { generator.generateFlat() }
            .map { flat -> flat.id to flat }
            .toMap()
    }

    fun findAll(): Map<Int, Flat> = data

    fun findOne(id: Int): Flat? = data[id]

}