package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.repository.QuizRepository

@Service
class QuizService(
    private val quizRepository: QuizRepository
) {

    fun getRecentApprovedQuizzes(userId: Int, limit: Int): List<Map<String, Any>> {
        return quizRepository.findApprovedQuizzesForUser(userId)
            .take(limit)
            .map { quiz ->
                mapOf(
                    "quiz_id" to quiz.quizId,
                    "course_name" to quiz.course.courseName,
                    "title" to quiz.title,
                    "creation_date" to quiz.creationDate
                )
            }
    }
}
