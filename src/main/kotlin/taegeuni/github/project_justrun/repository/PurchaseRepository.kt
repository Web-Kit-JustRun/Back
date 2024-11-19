package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.Purchase

interface PurchaseRepository : JpaRepository<Purchase, Int> {

    @Query("""
        SELECT p FROM Purchase p
        JOIN FETCH p.item i
        WHERE p.user.userId = :userId
        ORDER BY p.purchaseDate DESC
    """)
    fun findByUserId(@Param("userId") userId: Int): List<Purchase>
}
