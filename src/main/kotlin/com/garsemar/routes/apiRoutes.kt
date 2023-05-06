package com.garsemar.routes

import com.garsemar.db.UsersDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.apiRouting() {
    val usersDao = UsersDao()
    route("/api") {
        authenticate("auth-admin") {
            get("toggleBlock{name?}") {
                val name = call.parameters["name"] ?: return@get call.respondText(
                    "Missing name",
                    status = HttpStatusCode.BadRequest
                )
                usersDao.alterBlock(name, usersDao.list().find { it.name == name }!!.blocked)
                call.respondRedirect("/admin")
            }
        }
    }
}