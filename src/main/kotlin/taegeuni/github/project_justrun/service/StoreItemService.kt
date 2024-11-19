package taegeuni.github.project_justrun.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import taegeuni.github.project_justrun.dto.PurchaseHistoryResponseItem
import taegeuni.github.project_justrun.dto.PurchaseRequest
import taegeuni.github.project_justrun.dto.PurchaseResponse
import taegeuni.github.project_justrun.dto.StoreItemResponse
import taegeuni.github.project_justrun.entity.Purchase
import taegeuni.github.project_justrun.repository.PurchaseRepository
import taegeuni.github.project_justrun.repository.StoreItemRepository
import taegeuni.github.project_justrun.repository.UserRepository

@Service
class StoreItemService(
    private val storeItemRepository: StoreItemRepository,
    private val userRepository: UserRepository,
    private val purchaseRepository: PurchaseRepository
) {
    //전체 아이템 조회
    fun getAllItems(): List<StoreItemResponse> {
        val items = storeItemRepository.findAll()
        return items.map { it.toResponse() }
    }

    //아이템 목록 조회
    fun getAllItemTypes(): List<String> {
        return storeItemRepository.findDistinctItemTypes()
    }

    //해당 목록의 전체 아이템
    fun getItemsByType(itemType: String): List<StoreItemResponse> {
        val items = storeItemRepository.findByItemType(itemType)
        if (items.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "No items found for the given item type.")
        }
        return items.map { it.toResponse() }
    }

    //아이템 구매
    @Transactional
    fun purchaseItem(userId: Int, itemId: Int, request: PurchaseRequest): PurchaseResponse {
        // 1. 사용자 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        // 2. 아이템 조회
        val item = storeItemRepository.findById(itemId)
            .orElseThrow { NoSuchElementException("아이템을 찾을 수 없습니다.") }

        // 3. 총 가격 계산
        val totalPrice = item.price * request.quantity

        // 4. 포인트 충분 여부 확인
        if (user.rewardPoints < totalPrice) {
            throw IllegalArgumentException("리워드 포인트가 부족합니다.")
        }

        // 5. 포인트 차감
        user.rewardPoints -= totalPrice
        userRepository.save(user)

        // 6. 구매 내역 저장
        val purchase = Purchase(
            user = user,
            item = item,
            price = item.price,
            quantity = request.quantity,
            totalPrice = totalPrice,
            isUsed = false
        )
        val savedPurchase = purchaseRepository.save(purchase)

        // 7. 응답 생성
        return PurchaseResponse(
            message = "Item purchased successfully.",
            purchaseId = savedPurchase.purchaseId,
            purchasedQuantity = savedPurchase.quantity,
            remainingRewardPoints = user.rewardPoints
        )
    }

    //구매내역 조회
    @Transactional(readOnly = true)
    fun getPurchaseHistory(requestingUserId: Int, targetUserId: Int): List<PurchaseHistoryResponseItem> {
        // 1. 다른 사용자의 구매 내역 조회 방지
        if (requestingUserId != targetUserId) {
            throw IllegalAccessException("다른 사용자의 구매 내역은 조회할 수 없습니다.")
        }

        // 2. 사용자 존재 여부 확인
        val user = userRepository.findById(targetUserId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        // 3. 구매 내역 조회
        val purchases = purchaseRepository.findByUserId(targetUserId)

        // 4. 응답 생성
        return purchases.map { purchase ->
            PurchaseHistoryResponseItem(
                purchaseId = purchase.purchaseId,
                itemName = purchase.item.itemName,
                purchaseDate = purchase.purchaseDate,
                itemType = purchase.item.itemType,
                price = purchase.price,
                quantity = purchase.quantity,
                totalPrice = purchase.totalPrice,
                isUsed = purchase.isUsed
            )
        }
    }
}
