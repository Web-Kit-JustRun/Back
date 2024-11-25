package taegeuni.github.project_justrun.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import taegeuni.github.project_justrun.dto.AssignmentCreateRequest
import taegeuni.github.project_justrun.dto.AssignmentCreateResponse
import taegeuni.github.project_justrun.dto.AssignmentListResponseItemForProfessor
import taegeuni.github.project_justrun.dto.AssignmentListResponseItemForStudent
import taegeuni.github.project_justrun.entity.Assignment
import taegeuni.github.project_justrun.entity.Course
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType
import taegeuni.github.project_justrun.exception.BadRequestException
import taegeuni.github.project_justrun.exception.ForbiddenException
import taegeuni.github.project_justrun.repository.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Service
class AssignmentService(
    private val assignmentRepository: AssignmentRepository,
    private val assignmentSubmissionRepository: AssignmentSubmissionRepository,
    private val courseRepository: CourseRepository,
    private val userRepository: UserRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val fileStorageService: FileStorageService
) {
    @Value("\${app.upload.dir}")
    private val uploadPath: String = "./files"

    private val logger: Logger = LoggerFactory.getLogger(javaClass)

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


    private fun getAssignmentListForProfessor(
        user: User,
        course: Course
    ): List<AssignmentListResponseItemForProfessor> {
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
        val assignment = findValidAssignmentByUserIdAndAssignmentId(userId, assignmentId)
        return assignment
    }

    fun submitAssignment(file: MultipartFile?, content: String?, assignmentId: Int, userId: Int) {
        if (file == null && content == null) {
            throw BadRequestException("파일과 본문 중 하나 이상의 필드는 존재해야 합니다.")
        }
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val assignment = findValidAssignmentByUserIdAndAssignmentId(userId, assignmentId)

        if (file != null) {
            val path = Paths.get(uploadPath, assignment.course.courseName)
            val currentDateTime = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm:ss")
            val formattedDateTime = currentDateTime.format(formatter)
            val filename = user.name + "_" + formattedDateTime + "_" + file.originalFilename

            Files.createDirectories(path)

            try {
                val savedFile = File(path.toRealPath().toString(), filename)
                file.transferTo(savedFile)
            } catch (e: Exception) {
                logger.error("Error while transfer to $filename", e)
                throw Error("Error on saving file")
            }
        }

        //TODO: 과제 실제로 제출하는 로직 작성
    }

    private fun findValidAssignmentByUserIdAndAssignmentId(
        userId: Int,
        assignmentId: Int
    ): Assignment {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val assignment = assignmentRepository.findTopByAssignmentId(assignmentId)
            ?: throw NoSuchElementException("과제를 찾을 수 없습니다.")

        val enrollments = enrollmentRepository.findAllByUserUserId(user.userId)

        val isPermitted = enrollments.any {
            it.course.courseId == assignment.course.courseId
        }

        if (!isPermitted) {
            throw ForbiddenException("권한이 없습니다.")
        }
        return assignment
    }

    //교수가 과제 생성
    @Transactional
    fun createAssignment(
        userId: Int,
        courseId: Int,
        request: AssignmentCreateRequest,
        attachmentFile: MultipartFile?
    ): AssignmentCreateResponse {
        // 1. 사용자 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        // 2. 강의 조회
        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 3. 사용자가 해당 강의의 교수인지 확인
        if (user.userType != UserType.professor || course.professor.userId != user.userId) {
            throw IllegalAccessException("해당 강의에 대한 접근 권한이 없습니다.")
        }

        // 4. 첨부파일 처리
        var attachmentPath: String? = null
        var attachmentName: String? = null

        if (attachmentFile != null && !attachmentFile.isEmpty) {
            try {
                // 파일 저장 서비스 사용
                attachmentPath = fileStorageService.storeAssignmentFile(attachmentFile)
                attachmentName = attachmentFile.originalFilename
            } catch (ex: IOException) {
                throw RuntimeException("파일 저장 중 오류가 발생했습니다.", ex)
            }
        }

        // 5. 과제 생성 및 저장
        val assignment = Assignment(
            course = course,
            title = request.title,
            content = request.content,
            attachment = attachmentPath,
            attachmentName = attachmentName,
            dueDate = request.dueDate,
            maxScore = request.maxScore
        )

        val savedAssignment = assignmentRepository.save(assignment)

        // 6. 응답 생성
        return AssignmentCreateResponse(
            message = "Assignment created successfully.",
            assignmentId = savedAssignment.assignmentId
        )
    }

}
