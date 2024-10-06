package com.nigel.ecommerce.models

import org.json.JSONObject
import java.io.Serializable

data class Review(
    val id: String,
    val customerId: String,
    val productId: String,
    val message: String,
    val rating: Double,
) : Serializable {

    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("customerId", customerId)
            put("productId", productId)
            put("message", message)
            put("rating", rating)
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "customerId" to customerId,
            "productId" to productId,
            "message" to message,
            "rating" to rating
        )
    }
}
