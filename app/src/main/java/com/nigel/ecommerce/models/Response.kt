package com.nigel.ecommerce.models

data class ApiResponse(
    val Success: Boolean,
    val Message: String,
    val Data: TokenResponse?,
    val Errors: List<Any>
)

data class TokenResponse(
    val Token: String,
    val Role: String,
    val Name: String
)


