package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taegeuni.github.project_justrun.dto.*
import taegeuni.github.project_justrun.entity.*
import taegeuni.github.project_justrun.exception.ForbiddenException
import taegeuni.github.project_justrun.repository.*

@Service
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentSubmissionRepository: AssignmentSubmissionRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val enrollmentRepository: EnrollmentRepository
) {

    @Transactional(readOnly = true)
    fun getAssignmentList(userId: Int, courseId: Int): List<Any> {
        // 1. 사용자 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        // 2. 강의 조회
        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 3. 사용자 타입에 따른 처리
        return when (user.userType) {
            UserType.student -> getAssignmentListForStudent(user, course)
            UserType.professor -> getAssignmentListForProfessor(user, course)
        }
    }

    private fun getAssignmentListForStudent(user: User, course: Course): List<AssignmentListResponseItemForStudent> {
        // 1. 해당 강의의 과제 목록 조회
        val assignments = assignmentRepository.findByCourseId(course.courseId)

        // 2. 각 과제에 대한 제출 정보 조회
        val assignmentIds = assignments.map { it.assignmentId }
        val submissions = if (assignmentIds.isNotEmpty()) {
            assignmentSubmissionRepository.findByAssignmentAssignmentIdInAndStudentUserId(assignmentIds, user.userId)
                .associateBy { it.assignment.assignmentId }
        } else {
            emptyMap()
        }

        // 3. 응답 생성
        return assignments.map { assignment ->
            val submission = submissions[assignment.assignmentId]
            AssignmentListResponseItemForStudent(
                assignmentId = assignment.assignmentId,
                title = assignment.title,
                dueDate = assignment.dueDate,
                isSubmitted = submission != null,
                score = submission?.score
            )
        }
    }


    private fun getAssignmentListForProfessor(user: User, course: Course): List<AssignmentListResponseItemForProfessor> {
        // 1. 사용자가 해당 강의의 교수인지 확인
        if (course.professor.userId != user.userId) {
            throw IllegalAccessException("해당 강의에 대한 접근 권한이 없습니다.")
        }

        // 2. 해당 강의의 과제 목록 조회
        val assignments = assignmentRepository.findByCourseId(course.courseId)

        // 3. 응답 생성
        return assignments.map { assignment ->
            AssignmentListResponseItemForProfessor(
                assignmentId = assignment.assignmentId,
                title = assignment.title,
                dueDate = assignment.dueDate
            )
        }
    }

    @Transactional(readOnly = true)
    fun getAssignment(userId: Int, assignmentId: Int): Assignment? {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val assignment = assignmentRepository.findTopByAssignmentId(assignmentId)
            ?: throw NoSuchElementException("과제를 찾을 수 없습니다.")

        val enrollments = enrollmentRepository.findAllByUserUserId(user.userId)

        val isPermitted =  enrollments.any {
            it.course.courseId == assignment.course.courseId
        }

        if (!isPermitted) {
            throw ForbiddenException("권한이 없습니다.")
        }

        return assignment
    }
}
