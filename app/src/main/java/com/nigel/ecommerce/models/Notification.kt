package com.nigel.ecommerce.models

import java.io.Serializable

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val IsRead: Boolean,
    val userId: String,
    val createdOn: String,
) : Serializable
