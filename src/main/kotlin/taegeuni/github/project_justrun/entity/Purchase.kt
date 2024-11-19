package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "purchase")
data class Purchase(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val purchaseId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    val item: StoreItem,

    @Column(nullable = false)
    val purchaseDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val price: Int, // 구매 당시 가격 (단위 가격)

    @Column(nullable = false)
    val quantity: Int, // 구매 수량

    @Column(nullable = false)
    val totalPrice: Int, // 총 가격 (price * quantity)

    @Column(nullable = false)
    val isUsed: Boolean = false // 아이템 사용 여부
)
