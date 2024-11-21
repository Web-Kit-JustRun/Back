package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class QuizDetailResponse(
    val quizId: Int,
    val title: String,
    val question: String,
    val choices: List<String>,
    val correctChoice: Int, // 학생인 경우 -1로 설정
    val creationDate: LocalDateTime,
    val status: String,
    val creator: CreatorInfo,
    val attempt_status: String // "not_attempted", "correct", "incorrect"
)
