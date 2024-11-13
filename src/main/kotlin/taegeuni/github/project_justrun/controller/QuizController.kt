package taegeuni.github.project_justrun.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import taegeuni.github.project_justrun.service.QuizService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api/quizzes")
class QuizController(
    private val quizService: QuizService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/recent")
    fun getRecentQuizzes(
        @RequestHeader("Authorization") token: String,
        @RequestParam("limit", defaultValue = "5") limit: Int
    ): ResponseEntity<List<Map<String, Any>>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val quizzes = quizService.getRecentApprovedQuizzes(userId, limit)
        return ResponseEntity.ok(quizzes)
    }
}
