package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import taegeuni.github.project_justrun.entity.Course
import taegeuni.github.project_justrun.entity.Enrollment
import taegeuni.github.project_justrun.entity.User

interface EnrollmentRepository : JpaRepository<Enrollment, Int> {
    fun findAllByUserUserId(userId: Int): List<Enrollment>
    fun existsByUserAndCourse(user: User, course: Course): Boolean
}
