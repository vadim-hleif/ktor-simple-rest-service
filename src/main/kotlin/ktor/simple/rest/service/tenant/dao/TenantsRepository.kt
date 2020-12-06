package ktor.simple.rest.service.tenant.dao

import ktor.simple.rest.service.tenant.dtos.Tenant

object TenantsRepository {

    private val data by lazy { (1..5).map { Tenant() } }

    fun findAll(): List<Tenant> = data

    fun findOne(id: Int): Tenant? = data.find { it.id == id }

}