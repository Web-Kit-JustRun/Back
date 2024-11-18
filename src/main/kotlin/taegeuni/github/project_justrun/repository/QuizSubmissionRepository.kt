package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.QuizSubmission

interface QuizSubmissionRepository : JpaRepository<QuizSubmission, Int> {
    @Query("""
        SELECT qs FROM QuizSubmission qs
        WHERE qs.quiz.quizId = :quizId AND qs.student.userId = :studentId
    """)
    fun findByQuizIdAndStudentId(
        @Param("quizId") quizId: Int,
        @Param("studentId") studentId: Int
    ): QuizSubmission?
}
