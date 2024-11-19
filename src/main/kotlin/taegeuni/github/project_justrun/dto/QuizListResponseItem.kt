package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class QuizListResponseItem(
    val quizId: Int,
    val title: String,
    val creationDate: LocalDateTime,
    val attemptStatus: String // "not_attempted", "correct", "incorrect"
)
