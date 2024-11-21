package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class AssignmentListResponseItemForStudent(
    val assignmentId: Int,
    val title: String,
    val dueDate: LocalDateTime,
    val isSubmitted: Boolean,
    val score: Int?
)
