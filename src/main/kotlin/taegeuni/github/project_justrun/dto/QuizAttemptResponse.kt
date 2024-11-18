package taegeuni.github.project_justrun.dto

data class QuizAttemptResponse(
    val isCorrect: Boolean,
    val message: String,
    val pointsAwarded: Int
)
