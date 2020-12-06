package ktor.simple.rest.service.landlord.dao

import ktor.simple.rest.service.landlord.dtos.Landlord

object LandlordsRepository {

    private val data by lazy { (1..3).map { Landlord() } }

    fun findAll(): List<Landlord> = data

}