package taegeuni.github.project_justrun.dto

data class PurchaseResponse(
    val message: String,
    val purchaseId: Int,
    val purchasedQuantity: Int,
    val remainingRewardPoints: Int
)
