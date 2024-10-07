package com.nigel.ecommerce.models

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

data class Order(
    val orderId: String,
    val orderNo: String,
    val customerNo: String,
    var deliveryAddress: String,
    val orderDate: String,
    val status: String,
    var isCancelRequested: Boolean?,
    var orderLines: List<OrderItem>,
) : Serializable {
    // Convert Order object to a JSON object
    fun toJson(): JSONObject {
        return JSONObject().apply {
            put("orderId", orderId)
            put("orderNo", orderNo)
            put("customerNo", customerNo)
            put("deliveryAddress", deliveryAddress)
            put("orderDate", orderDate)
            put("status", status)


            val orderLinesJsonArray = JSONArray().apply {
                orderLines.forEach { orderItem ->
                    put(orderItem.toJson())
                }
            }
            put("orderLines", orderLinesJsonArray)
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf<String, Any>(
            "orderId" to orderNo,
            "orderNo" to orderNo,
            "customerNo" to customerNo,
            "deliveryAddress" to deliveryAddress,
            "orderDate" to orderDate,
            "status" to status
        )
    }
}
