package taegeuni.github.project_justrun.dto

data class StoreItemResponse(
    val itemId: Int,
    val itemName: String,
    val itemDescription: String?,
    val itemType: String?,
    val price: Int,
    val imageUrl: String?
)

