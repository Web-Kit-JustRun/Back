package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class QuizSummary(
    val quizId: Int,
    val title: String,
    val creationDate: LocalDateTime,
    val status: String
)
