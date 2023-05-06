package com.garsemar.model

import io.ktor.server.auth.*
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Query

object Users : IntIdTable(), Principal {
    fun Query.mapUsers(): List<User> =
        this.map {
            User(
                it[sequelId],
                it[name],
                it[password],
                it[admin],
                it[blocked]
            )
        }

    val sequelId = integer("sequel_id").uniqueIndex().autoIncrement()
    val name = varchar("name", 50).uniqueIndex()
    val password = varchar("password", 50)
    val admin = bool("admin").default(false)
    val blocked = bool("blocked").default(false)
}