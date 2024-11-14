package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.Course

interface CourseRepository : JpaRepository<Course, Int> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Enrollment e WHERE e.course.courseId = :courseId AND e.user.userId = :userId")
    fun isUserEnrolledInCourse(@Param("courseId") courseId: Int, @Param("userId") userId: Int): Boolean
}
