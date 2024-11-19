package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class PurchaseHistoryResponseItem(
    val purchaseId: Int,
    val itemName: String,
    val purchaseDate: LocalDateTime,
    val itemType: String?,
    val price: Int,
    val quantity: Int,
    val totalPrice: Int,
    val isUsed: Boolean
)
