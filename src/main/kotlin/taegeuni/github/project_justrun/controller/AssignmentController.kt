package taegeuni.github.project_justrun.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.service.AssignmentService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api")
class AssignmentController(
    private val assignmentService: AssignmentService,
    private val jwtUtil: JwtUtil
) {

    @GetMapping("/courses/{courseId}/assignments")
    fun getAssignmentList(
        @PathVariable courseId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<Any>> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val assignmentList = assignmentService.getAssignmentList(userId, courseId)
        return ResponseEntity.ok(assignmentList)
    }
}
