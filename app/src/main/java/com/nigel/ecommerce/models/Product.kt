package com.nigel.ecommerce.models

import java.io.Serializable

data class Product(
    val id: String,
    val title: String,
    val imageURL: List<String>,
    var category: String,
    val subCategory: String,
    val price: Double,
    val description: String,
    val active: Boolean,
    val stockCount: Double,
    val vendorId: String,
    val lowStockThreshold: Double,
    val isPartOfPendingOrder: Boolean,
    val subCategoryName: String?
): Serializable
