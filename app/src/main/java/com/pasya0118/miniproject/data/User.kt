package com.pasya0118.miniproject.data

data class User(
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
) {
    override fun toString(): String {
        return "User(name=$name, email=$email, photoUrl=$photoUrl)"
    }
}
