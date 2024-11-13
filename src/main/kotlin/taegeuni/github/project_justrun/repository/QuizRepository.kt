package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.Quiz

interface QuizRepository : JpaRepository<Quiz, Int> {

    @Query("""
        SELECT q FROM Quiz q 
        WHERE q.status = 'approved' 
        AND q.course.courseId IN (
            SELECT e.course.courseId FROM Enrollment e WHERE e.user.userId = :userId
        )
        ORDER BY q.creationDate DESC
    """)
    fun findApprovedQuizzesForUser(@Param("userId") userId: Int): List<Quiz>
}
