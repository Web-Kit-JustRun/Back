package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.Quiz
import taegeuni.github.project_justrun.entity.QuizStatus

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

    @Query("""
        SELECT q FROM Quiz q
        JOIN FETCH q.course c
        WHERE c.courseId = :courseId AND q.status = 'approved'
        ORDER BY q.creationDate DESC
    """)
    fun findApprovedQuizzesByCourseId(@Param("courseId") courseId: Int): List<Quiz>

    @Query("""
        SELECT q FROM Quiz q
        WHERE q.course.courseId = :courseId
        AND q.status = :status
        ORDER BY q.creationDate DESC
    """)
    fun findQuizzesByCourseIdAndStatus(
        @Param("courseId") courseId: Int,
        @Param("status") status: QuizStatus
    ): List<Quiz>

    fun findByQuizIdAndCourseCourseId(quizId: Int, courseId: Int): Quiz?

    fun findByQuizIdAndStatus(quizId: Int, status: QuizStatus): Quiz?
}
