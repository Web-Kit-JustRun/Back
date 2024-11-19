package taegeuni.github.project_justrun.service

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import taegeuni.github.project_justrun.dto.StoreItemResponse
import taegeuni.github.project_justrun.repository.StoreItemRepository

@Service
class StoreItemService(
    private val storeItemRepository: StoreItemRepository
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

}
