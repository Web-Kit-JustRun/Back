package taegeuni.github.project_justrun.controller

import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.dto.PurchaseRequest
import taegeuni.github.project_justrun.dto.PurchaseResponse
import taegeuni.github.project_justrun.dto.StoreItemResponse
import taegeuni.github.project_justrun.service.StoreItemService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api/store")
class StoreItemController(
    private val storeItemService: StoreItemService,
    private val jwtUtil: JwtUtil
) {
    //전체 아이템 조회
    @GetMapping("/items")
    fun getAllStoreItems(): ResponseEntity<List<StoreItemResponse>> {
        val items = storeItemService.getAllItems()
        return ResponseEntity.ok(items)
    }

    //아이템 목록 조회
    @GetMapping("/item_types")
    fun getItemTypes(): ResponseEntity<Map<String, List<String>>> {
        val itemTypes = storeItemService.getAllItemTypes()
        return ResponseEntity.ok(mapOf("item_types" to itemTypes))
    }

    //해당 목록의 전체 아이템 조회
    @GetMapping("/items/{item_type}")
    fun getItemsByType(@PathVariable("item_type") itemType: String): ResponseEntity<List<StoreItemResponse>> {
        val items = storeItemService.getItemsByType(itemType)
        return ResponseEntity.ok(items)
    }

    @PostMapping("/items/{itemId}/purchase")
    fun purchaseItem(
        @PathVariable itemId: Int,
        @Validated @RequestBody request: PurchaseRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<PurchaseResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val response = storeItemService.purchaseItem(userId, itemId, request)
        return ResponseEntity.ok(response)
    }

}
