package com.garsemar

import com.garsemar.db.H2Config
import com.garsemar.db.UsersDao
import com.garsemar.model.User
import com.garsemar.model.UserSession
import com.garsemar.model.Users
import com.garsemar.model.Users.mapUsers
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.jetty.*
import com.garsemar.plugins.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun main() {
    embeddedServer(Jetty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    val usersDao = UsersDao()
    H2Config().configH2()

    var users: List<User> = usersDao.list()

    install(Authentication) {
        session<UserSession>("auth-session") {
            validate { session ->
                if(session.name.isNotEmpty() && !usersDao.checkBlocked(session.name)) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
        session<UserSession>("auth-admin") {
            validate { session ->
                if(usersDao.checkAdmin(session.name) && usersDao.checkBlocked(session.name)) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/home")
            }
        }
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                if (users.any { it.name == credentials.name } && users.any { it.password == credentials.password }) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
        form("signup-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                usersDao.addUser(credentials.name, credentials.password)
                users = usersDao.list()
                if (users.any { it.name == credentials.name } && users.any { it.password == credentials.password }) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/home")
            }
        }
    }
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 340
        }
    }

    configureRouting()
}