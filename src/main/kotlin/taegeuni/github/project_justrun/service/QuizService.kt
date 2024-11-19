package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taegeuni.github.project_justrun.dto.*
import taegeuni.github.project_justrun.entity.*
import taegeuni.github.project_justrun.repository.*
import java.time.LocalDateTime


@Service
class QuizService(
    private val quizRepository: QuizRepository,
    private val courseRepository: CourseRepository,
    private val enrollmentRepository: EnrollmentRepository,
    private val userRepository: UserRepository,
    private val quizSubmissionRepository: QuizSubmissionRepository,
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

    @Transactional
    fun approveOrRejectQuiz(
        userId: Int,
        quizId: Int,
        request: QuizApprovalRequest
    ): QuizApprovalResponse {
        // 1. 사용자 조회 및 권한 확인
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        if (user.userType != UserType.professor) {
            throw IllegalAccessException("교수 권한이 필요합니다.")
        }

        // 2. 퀴즈 조회 및 상태 확인
        val quiz = quizRepository.findByQuizIdAndStatus(quizId, QuizStatus.pending)
            ?: throw NoSuchElementException("대기 중인 퀴즈를 찾을 수 없습니다.")

        // 3. 사용자가 해당 강의의 교수인지 확인
        val course = quiz.course
        if (course.professor.userId != userId) {
            throw IllegalAccessException("해당 퀴즈에 대한 권한이 없습니다.")
        }

        // 4. 승인 또는 거절 처리
        return if (request.isApprove == "approve") {
            if (request.points == null || request.points <= 0) {
                throw IllegalArgumentException("승인 시에는 유효한 포인트가 필요합니다.")
            }

            // 퀴즈 상태 업데이트
            quiz.status = QuizStatus.approved
            quiz.points = request.points
            quizRepository.save(quiz)

            // 학생의 포인트 업데이트
            val creator = quiz.creator
            creator.rankingPoints += request.points
            creator.rewardPoints += request.points
            userRepository.save(creator)

            QuizApprovalResponse(message = "Quiz approved successfully.")
        } else if (request.isApprove == "reject") {
            // 퀴즈 상태 업데이트
            quiz.status = QuizStatus.rejected
            quiz.points = null
            quizRepository.save(quiz)

            QuizApprovalResponse(message = "Quiz rejected successfully.")
        } else {
            throw IllegalArgumentException("isApprove 필드는 'approve' 또는 'reject'여야 합니다.")
        }
    }

    @Transactional
    fun attemptQuiz(userId: Int, quizId: Int, request: QuizAttemptRequest): QuizAttemptResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        if (user.userType != UserType.student) {
            throw IllegalAccessException("학생만 퀴즈를 풀 수 있습니다.")
        }

        val quiz = quizRepository.findById(quizId)
            .orElseThrow { NoSuchElementException("퀴즈를 찾을 수 없습니다.") }

        if (quiz.status != QuizStatus.approved) {
            throw IllegalAccessException("승인된 퀴즈만 풀 수 있습니다.")
        }

        if (request.selectedChoice !in 1..4) {
            throw IllegalArgumentException("선택한 답안 번호는 1에서 4 사이여야 합니다.")
        }

        val isCorrect = request.selectedChoice == quiz.correctChoice
        var pointsAwarded = 0

        val existingSubmission = quizSubmissionRepository.findByQuizIdAndStudentId(quizId, userId)

        if (existingSubmission != null) {
            // 제출 기록 업데이트
            existingSubmission.selectedChoice = request.selectedChoice
            existingSubmission.isCorrect = isCorrect
            existingSubmission.submissionDate = LocalDateTime.now()

            if (isCorrect && !existingSubmission.pointsAwarded) {
                // 최초 정답 시 포인트 지급
                val points = quiz.points ?: 0
                user.rankingPoints += points
                user.rewardPoints += points
                userRepository.save(user)

                existingSubmission.pointsAwarded = true
                pointsAwarded = points
            }

            quizSubmissionRepository.save(existingSubmission)
        } else {
            // 새로운 제출 기록 생성
            val submission = QuizSubmission(
                quiz = quiz,
                student = user,
                selectedChoice = request.selectedChoice,
                isCorrect = isCorrect,
                submissionDate = LocalDateTime.now(),
                pointsAwarded = false
            )

            if (isCorrect) {
                // 최초 정답 시 포인트 지급
                val points = quiz.points ?: 0
                user.rankingPoints += points
                user.rewardPoints += points
                userRepository.save(user)

                submission.pointsAwarded = true
                pointsAwarded = points
            }

            quizSubmissionRepository.save(submission)
        }

        val message = if (isCorrect) "Correct answer!" else "Incorrect answer."
        return QuizAttemptResponse(
            isCorrect = isCorrect,
            message = message,
            pointsAwarded = pointsAwarded
        )
    }

    @Transactional(readOnly = true)
    fun getApprovedQuizzesWithAttemptStatus(userId: Int, courseId: Int): List<QuizListResponseItem> {
        // 1. 사용자 확인
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        if (user.userType != UserType.student) {
            throw IllegalAccessException("학생만 퀴즈 목록을 조회할 수 있습니다.")
        }

        // 2. 승인된 퀴즈 목록 조회
        val quizzes = quizRepository.findApprovedQuizzesByCourseId(courseId)

        // 3. 퀴즈 ID 목록 추출
        val quizIds = quizzes.map { it.quizId }

        // 4. 학생의 제출 기록 조회
        val submissions = if (quizIds.isNotEmpty()) {
            quizSubmissionRepository.findByStudentIdAndQuizIds(userId, quizIds)
        } else {
            emptyList()
        }

        // 5. 제출 기록을 맵으로 변환 (quizId를 키로)
        val submissionMap = submissions.associateBy { it.quiz.quizId }

        // 6. 응답 생성
        return quizzes.map { quiz ->
            val submission = submissionMap[quiz.quizId]
            val attemptStatus = when {
                submission == null -> "not_attempted"
                submission.isCorrect -> "correct"
                else -> "incorrect"
            }
            QuizListResponseItem(
                quizId = quiz.quizId,
                title = quiz.title,
                creationDate = quiz.creationDate,
                attemptStatus = attemptStatus
            )
        }
    }


}
