package taegeuni.github.project_justrun.dto

import java.time.LocalDateTime

data class UserQuizResponseItem(
    val quizId: Int,
    val title: String,
    val creationDate: LocalDateTime,
    val status: String,
    val course: CourseInfo
)

data class CourseInfo(
    val courseId: Int,
    val courseName: String
)
