package com.garsemar.plugins

import com.garsemar.routes.apiRouting
import com.garsemar.routes.homeRouting
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        homeRouting()
        apiRouting()
    }
}
