package com.garsemar.routes

import com.garsemar.db.UsersDao
import com.garsemar.model.UserSession
import com.garsemar.model.Users.mapUsers
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*

fun Route.homeRouting() {
    val usersDao = UsersDao()
    route("/") {
        get("") {
            call.respondHtml {
                body {
                    a(href = "/login") { +"Login" }
                    br { }
                    a(href = "/signup") { +"Register" }
                }
            }
        }
        get("login") {
            call.respondHtml {
                body {
                    form(action = "/auth", method = FormMethod.post) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Password:"
                            passwordInput(name = "password")
                        }
                        p {
                            submitInput { value = "Login" }
                        }
                    }
                }
            }
        }
        get("signup") {
            call.respondHtml {
                body {
                    form(action = "/signup-auth", method = FormMethod.post) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Password:"
                            passwordInput(name = "password")
                        }
                        p {
                            submitInput { value = "Sign Up" }
                        }
                    }
                }
            }
        }
        authenticate("auth-form") {
            post("/auth") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(UserSession(name = userName, count = 1))
                call.respondRedirect("/home")
            }
        }
        authenticate("signup-form") {
            post("/signup-auth") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(UserSession(name = userName, count = 1))
                call.respondRedirect("/home")
            }
        }
        authenticate("auth-session") {
            get("/home") {
                val userSession = call.principal<UserSession>()
                call.sessions.set(userSession?.copy(count = userSession.count + 1))
                call.respondHtml {
                    body {
                        p {
                            +"Hello, ${userSession?.name}! Visit count is ${userSession?.count}."
                        }
                        a {
                            href = "/logout"
                            +"Log Out"
                        }
                    }
                }
            }
            get("/logout") {
                call.sessions.clear<UserSession>()
                call.respondRedirect("/")
            }
        }
        authenticate("auth-admin") {
            get("/admin") {
                val userSession = call.principal<UserSession>()
                val users = usersDao.list()
                println("------------- $users")
                call.respondHtml {
                    body {
                        h1 { +"Hola ${userSession?.name}" }
                        table {
                            thead {
                                tr {
                                    th { +"Name" }
                                    th { +"Blocked" }
                                }
                            }
                            tbody {
                                users.forEach {
                                    tr {
                                        td { +it.name }
                                        td {
                                            +"${it.blocked}"
                                            a(href = "/api/toggleBlock?name=${it.name}") { +"Toggle block" }
                                        }
                                    }
                                }
                            }
                        }
                        a {
                            href = "/logout"
                            +"Log Out"
                        }
                    }
                }
            }
        }
    }
}