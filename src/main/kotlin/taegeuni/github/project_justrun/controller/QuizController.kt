package taegeuni.github.project_justrun.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.dto.*
import taegeuni.github.project_justrun.service.QuizService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api")
class QuizController(
    private val quizService: QuizService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/quizzes/recent")
    fun getRecentQuizzes(
        @RequestHeader("Authorization") token: String,
        @RequestParam("limit", defaultValue = "5") limit: Int
    ): ResponseEntity<List<Map<String, Any>>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizzes = quizService.getRecentApprovedQuizzes(userId, limit)
        return ResponseEntity.ok(quizzes)
    }

    @PostMapping("/courses/{courseId}/quizzes/add")
    fun createQuiz(
        @PathVariable courseId: Int,
        @Validated @RequestBody quizRequest: QuizRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<QuizResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val user = quizService.getUserById(userId)
        val response = quizService.createQuiz(user, courseId, quizRequest)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/courses/{courseId}/quizzes")
    fun getApprovedQuizzesWithAttemptStatus(
        @PathVariable courseId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<QuizListResponseItem>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizList = quizService.getApprovedQuizzesWithAttemptStatus(userId, courseId)
        return ResponseEntity.ok(quizList)
    }

    @GetMapping("/courses/{courseId}/quizzes/pending")
    fun getPendingQuizzesForProfessor(
        @PathVariable courseId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<PendingQuizResponse>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizzes = quizService.getPendingQuizzesForProfessor(userId, courseId)
        return ResponseEntity.ok(quizzes)
    }

    @GetMapping("/courses/{courseId}/quizzes/{quizId}")
    fun getQuizDetailForProfessor(
        @PathVariable courseId: Int,
        @PathVariable quizId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<QuizDetailResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizDetail = quizService.getQuizDetailForProfessor(userId, courseId, quizId)
        return ResponseEntity.ok(quizDetail)
    }

    @PostMapping("/quizzes/{quizId}/approve")
    fun approveOrRejectQuiz(
        @PathVariable quizId: Int,
        @Validated @RequestBody request: QuizApprovalRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<QuizApprovalResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val response = quizService.approveOrRejectQuiz(userId, quizId, request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/quizzes/{quizId}/attempt")
    fun attemptQuiz(
        @PathVariable quizId: Int,
        @Validated @RequestBody request: QuizAttemptRequest,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<QuizAttemptResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val response = quizService.attemptQuiz(userId, quizId, request)
        return ResponseEntity.ok(response)
    }

    //퀴즈 상세조회
    @GetMapping("/quizzes/{quizId}")
    fun getQuizDetail(
        @PathVariable quizId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<QuizDetailResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizDetail = quizService.getQuizDetail(userId, quizId)
        return ResponseEntity.ok(quizDetail)
    }
}
