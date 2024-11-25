package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class AssignmentCreateRequest(
    val title: String,
    val content: String,
    val maxScore: Int,
    val dueDate: LocalDateTime
)

