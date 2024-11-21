package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class AssignmentListResponseItemForProfessor(
    val assignmentId: Int,
    val title: String,
    val dueDate: LocalDateTime
)
