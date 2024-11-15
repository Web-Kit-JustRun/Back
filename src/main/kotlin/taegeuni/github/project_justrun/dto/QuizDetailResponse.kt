package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class QuizDetailResponse(
    val quizId: Int,
    val title: String,
    val question: String,
    val choices: List<String>,
    val correctChoice: Int,
    val creationDate: LocalDateTime,
    val status: String,
    val creator: CreatorInfo
)
