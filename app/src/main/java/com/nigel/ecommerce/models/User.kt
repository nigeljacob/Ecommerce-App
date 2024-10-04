package com.nigel.ecommerce.models

data class User(
    val id: String?,
    val name: String?,
    val email: String?,
    val role: String?,
    val status: String?,
    val active: Boolean,
)
