package com.example.fitpal

data class User(
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val isMale: Boolean? = null,
    val fp: Int = 0,
    val sports: Array<String>
)