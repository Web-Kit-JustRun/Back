package taegeuni.github.project_justrun.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import taegeuni.github.project_justrun.dto.StoreItemResponse
import taegeuni.github.project_justrun.service.StoreItemService

@RestController
@RequestMapping("/api/store")
class StoreItemController(
    private val storeItemService: StoreItemService
) {
    @GetMapping("/items")
    fun getAllStoreItems(): ResponseEntity<List<StoreItemResponse>> {
        val items = storeItemService.getAllItems()
        return ResponseEntity.ok(items)
    }
    @GetMapping("/item_types")
    fun getItemTypes(): ResponseEntity<Map<String, List<String>>> {
        val itemTypes = storeItemService.getAllItemTypes()
        return ResponseEntity.ok(mapOf("item_types" to itemTypes))
    }
}
