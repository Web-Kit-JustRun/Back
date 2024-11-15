package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime


data class PendingQuizResponse(
    val quizId: Int,
    val title: String,
    val creationDate: LocalDateTime,
    val status: String,
    val creator: CreatorInfo
)
data class CreatorInfo(
    val userId: Int,
    val name: String
)