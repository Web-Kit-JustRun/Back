package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.dto.StoreItemResponse
import taegeuni.github.project_justrun.repository.StoreItemRepository

@Service
class StoreItemService(
    private val storeItemRepository: StoreItemRepository
) {
    fun getAllItems(): List<StoreItemResponse> {
        val items = storeItemRepository.findAll()
        return items.map { it.toResponse() }
    }
    fun getAllItemTypes(): List<String> {
        return storeItemRepository.findDistinctItemTypes()
    }
}
