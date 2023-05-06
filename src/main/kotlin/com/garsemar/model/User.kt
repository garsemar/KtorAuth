package com.garsemar.model

data class User(val id: Int, val name: String, val password: String, val admin: Boolean, val blocked: Boolean)