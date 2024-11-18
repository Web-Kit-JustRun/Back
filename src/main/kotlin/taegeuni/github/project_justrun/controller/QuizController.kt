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

    @PostMapping("/courses/{courseId}/quizzes")
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
    fun getApprovedQuizzesByCourse(
        @PathVariable courseId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<QuizSummary>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizzes = quizService.getApprovedQuizzesByCourse(userId, courseId)
        return ResponseEntity.ok(quizzes)
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
}
