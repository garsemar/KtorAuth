package com.garsemar.db

import com.garsemar.model.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class H2Config {
    fun configH2(){
        Database.connect("jdbc:h2:./myh2file", "org.h2.Driver")

        transaction {
            SchemaUtils.create(Users)
        }
    }
}