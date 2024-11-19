package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import taegeuni.github.project_justrun.entity.StoreItem

@Repository
interface StoreItemRepository : JpaRepository<StoreItem, Int>{
    @Query("SELECT DISTINCT s.itemType FROM StoreItem s WHERE s.itemType IS NOT NULL")
    fun findDistinctItemTypes(): List<String>
}
