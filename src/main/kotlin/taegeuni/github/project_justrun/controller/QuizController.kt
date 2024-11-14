package taegeuni.github.project_justrun.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.dto.QuizRequest
import taegeuni.github.project_justrun.dto.QuizResponse
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
}
