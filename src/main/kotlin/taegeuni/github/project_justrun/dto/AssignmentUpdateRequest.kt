package taegeuni.github.project_justrun.dto

import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

data class AssignmentUpdateRequest(
    val title: String?,
    val content: String?,
    val maxScore: Int?,
    val dueDate: LocalDateTime?,
    val attachment: MultipartFile?
)
