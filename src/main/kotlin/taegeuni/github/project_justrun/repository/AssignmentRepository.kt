package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.Assignment

interface AssignmentRepository : JpaRepository<Assignment, Int> {

    @Query("""
        SELECT a FROM Assignment a
        WHERE a.course.courseId = :courseId
        ORDER BY a.dueDate ASC
    """)
    fun findByCourseId(@Param("courseId") courseId: Int): List<Assignment>

    fun findTopByAssignmentId(@Param("assignmentId") assignmentId: Int): Assignment?
}
