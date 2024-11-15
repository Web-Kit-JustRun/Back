package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taegeuni.github.project_justrun.dto.*
import taegeuni.github.project_justrun.entity.Quiz
import taegeuni.github.project_justrun.entity.QuizStatus
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType
import taegeuni.github.project_justrun.repository.CourseRepository
import taegeuni.github.project_justrun.repository.EnrollmentRepository
import taegeuni.github.project_justrun.repository.QuizRepository
import taegeuni.github.project_justrun.repository.UserRepository
import java.time.LocalDateTime


@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
) {

    fun getRecentApprovedQuizzes(userId: Int, limit: Int): List<Map<String, Any>> {
        return quizRepository.findApprovedQuizzesForUser(userId)
            .take(limit)
            .map { quiz ->
                mapOf(
                    "quiz_id" to quiz.quizId,
                    "course_name" to quiz.course.courseName,
                    "title" to quiz.title,
                    "creation_date" to quiz.creationDate
                )
            }
    }

    // 사용자 정보를 가져오는 메서드 추가
    fun getUserById(userId: Int): User {
        return userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }
    }

    @Transactional
    fun createQuiz(user: User, courseId: Int, quizRequest: QuizRequest): QuizResponse {
        // 1. 사용자가 학생인지 확인
        if (user.userType != UserType.student) {
            throw IllegalAccessException("학생만 퀴즈를 등록할 수 있습니다.")
        }

        // 2. 코스 존재 여부 확인
        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 3. 사용자가 해당 강의를 수강 중인지 확인
        val isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course)
        if (!isEnrolled) {
            throw IllegalAccessException("해당 강의에 등록되지 않았습니다.")
        }

        // 4. 퀴즈 엔티티 생성 및 저장
        val quiz = Quiz(
            course = course,
            title = quizRequest.title,
            question = quizRequest.question,
            choice1 = quizRequest.choices[0],
            choice2 = quizRequest.choices[1],
            choice3 = quizRequest.choices[2],
            choice4 = quizRequest.choices[3],
            correctChoice = quizRequest.correctChoice,
            creator = user,
            creationDate = LocalDateTime.now(),
            status = QuizStatus.pending // 기본 상태는 pending
        )

        val savedQuiz = quizRepository.save(quiz)

        // 5. 응답 생성
        return QuizResponse(
            message = "Quiz submitted successfully.",
            quizId = savedQuiz.quizId,
            status = savedQuiz.status.name // Enum의 이름을 반환
        )
    }

    @Transactional(readOnly = true)
    fun getApprovedQuizzesByCourse(userId: Int, courseId: Int): List<QuizSummary> {
        // 1. 사용자 및 강의 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 2. 사용자가 해당 강의에 수강 등록되어 있는지 확인
        val isEnrolled = enrollmentRepository.existsByUserAndCourse(user, course)
        if (!isEnrolled) {
            throw IllegalAccessException("해당 강의에 대한 접근 권한이 없습니다.")
        }

        // 3. 승인된 퀴즈 목록 조회
        val quizzes = quizRepository.findApprovedQuizzesByCourseId(courseId)

        // 4. DTO로 변환하여 반환
        return quizzes.map { quiz ->
            QuizSummary(
                quizId = quiz.quizId,
                title = quiz.title,
                creationDate = quiz.creationDate,
                status = quiz.status.name
            )
        }
    }

    @Transactional(readOnly = true)
    fun getPendingQuizzesForProfessor(userId: Int, courseId: Int): List<PendingQuizResponse> {
        // 1. 사용자 및 강의 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 2. 사용자가 해당 강의의 교수인지 확인
        if (course.professor.userId != user.userId) {
            throw IllegalAccessException("해당 강의에 대한 접근 권한이 없습니다.")
        }

        // 3. pending 상태의 퀴즈 목록 조회
        val quizzes = quizRepository.findQuizzesByCourseIdAndStatus(courseId, QuizStatus.pending)

        // 4. DTO로 변환하여 반환
        return quizzes.map { quiz ->
            PendingQuizResponse(
                quizId = quiz.quizId,
                title = quiz.title,
                creationDate = quiz.creationDate,
                status = quiz.status.name,
                creator = CreatorInfo(
                    userId = quiz.creator.userId,
                    name = quiz.creator.name
                )
            )
        }
    }

    @Transactional(readOnly = true)
    fun getQuizDetailForProfessor(userId: Int, courseId: Int, quizId: Int): QuizDetailResponse {
        // 1. 사용자 및 강의 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        val course = courseRepository.findById(courseId)
            .orElseThrow { NoSuchElementException("강의를 찾을 수 없습니다.") }

        // 2. 사용자가 해당 강의의 교수인지 확인
        if (course.professor.userId != user.userId) {
            throw IllegalAccessException("해당 강의에 대한 접근 권한이 없습니다.")
        }

        // 3. 퀴즈 조회
        val quiz = quizRepository.findByQuizIdAndCourseCourseId(quizId, courseId)
            ?: throw NoSuchElementException("퀴즈를 찾을 수 없습니다.")

        // 4. DTO로 변환하여 반환
        return QuizDetailResponse(
            quizId = quiz.quizId,
            title = quiz.title,
            question = quiz.question,
            choices = listOf(quiz.choice1, quiz.choice2, quiz.choice3, quiz.choice4),
            correctChoice = quiz.correctChoice,
            creationDate = quiz.creationDate,
            status = quiz.status.name,
            creator = CreatorInfo(
                userId = quiz.creator.userId,
                name = quiz.creator.name
            )
        )
    }

}
