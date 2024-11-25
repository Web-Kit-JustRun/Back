package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class AssignmentListItemForProfessorDto(
    val assignmentId: Int,
    val title: String,
    val dueDate: LocalDateTime,
    val score: Int
)

