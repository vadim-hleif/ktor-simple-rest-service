package ktor.simple.rest.service.utils.requests

import ktor.simple.rest.service.flat.dtos.ViewingSlotState

class ViewingSlotPatchRequest(val tenantId: Int?, val state: ViewingSlotState?) {

    init {
        if (tenantId != null && state != null) throw IllegalArgumentException("tenantId and state can't be provided together")
    }

}