package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.repository.EnrollmentRepository

@Service
class CourseService(
    private val enrollmentRepository: EnrollmentRepository
) {

    fun getCoursesForUser(userId: Int): List<Map<String, Any>> {
        val enrollments = enrollmentRepository.findAllByUserUserId(userId)
        return enrollments.map { enrollment ->
            mapOf(
                "courseId" to enrollment.course.courseId,
                "courseName" to enrollment.course.courseName
            )
        }
    }
}
