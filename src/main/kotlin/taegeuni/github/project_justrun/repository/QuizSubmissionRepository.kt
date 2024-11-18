package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import taegeuni.github.project_justrun.entity.QuizSubmission
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.Quiz

interface QuizSubmissionRepository : JpaRepository<QuizSubmission, Int> {
    @Query("""
        SELECT qs FROM QuizSubmission qs
        WHERE qs.quiz = :quiz AND qs.student = :student AND qs.isCorrect = true
    """)

    fun findByQuizAndStudent(quiz: Quiz, student: User): QuizSubmission?
}
