package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import taegeuni.github.project_justrun.entity.Purchase

interface PurchaseRepository : JpaRepository<Purchase, Int>
