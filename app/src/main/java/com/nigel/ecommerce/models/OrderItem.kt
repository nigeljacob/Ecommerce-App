package com.nigel.ecommerce.models

import org.json.JSONObject
import java.io.Serializable

data class OrderItem(
    val orderLineNo: String,
    val productNo: String,
    val vendorNo: String,
    val orderNo: String,
    val status: String,
    var qty: Double,
    val unitPrice: Double,
    val total: Double,
    val productName: String,
    val vendorName: String,
) : Serializable {
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("orderLineNo", orderLineNo)
            put("productNo", productNo)
            put("vendorNo", vendorNo)
            put("orderNo", orderNo)
            put("status", status)
            put("qty", qty)
            put("unitPrice", unitPrice)
            put("total", total)
            put("productName", productName)
            put("vendorName", vendorName)
        }
    }
}
