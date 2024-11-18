package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import taegeuni.github.project_justrun.dto.StoreItemResponse

@Entity
@Table(name = "store_item")
data class StoreItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val itemId: Int,

    @Column(nullable = false, length = 100)
    val itemName: String,

    @Column(columnDefinition = "TEXT")
    val itemDescription: String?,

    @Column(length = 50)
    val itemType: String?,

    @Column(nullable = false)
    val price: Int,

    @Column(length = 255)
    val itemImageUrl: String?
) {
    fun toResponse(): StoreItemResponse {
        return StoreItemResponse(
            itemId = itemId,
            itemName = itemName,
            itemDescription = itemDescription,
            itemType = itemType,
            price = price,
            imageUrl = itemImageUrl
        )
    }
}
