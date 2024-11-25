package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class AssignmentListItemForStudentDto(
    val assignmentId: Int,
    val title: String,
    val dueDate: LocalDateTime,
    val score: Int,
    val isSubmitted: Boolean,
    val isGraded: Boolean,
    val studentScore: Int?
)

