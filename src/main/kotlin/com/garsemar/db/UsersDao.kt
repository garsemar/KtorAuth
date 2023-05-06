package com.garsemar.db

import com.garsemar.model.User
import com.garsemar.model.Users
import com.garsemar.model.Users.mapUsers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class UsersDao {
    fun list(): List<User> = transaction {
        Users.selectAll().mapUsers()
    }

    fun checkAdmin(name: String): Boolean = transaction {
        Users.select { Users.name eq name }.first()[Users.admin]
    }

    fun checkBlocked(name: String): Boolean = transaction {
        Users.select { Users.name eq name }.first()[Users.blocked]
    }

    fun alterBlock(name: String, block: Boolean) = transaction {
        Users.update ({ Users.name eq name }) {
            it[blocked] = !block
        }
    }

    fun addUser(name: String, password: String) = transaction {
        try {
            Users.insertAndGetId {
                it[Users.name] = name
                it[Users.password] = password
            }
        }
        catch (e: ExposedSQLException){
            println(e)
        }
    }
}