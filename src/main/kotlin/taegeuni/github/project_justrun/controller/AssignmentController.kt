package taegeuni.github.project_justrun.controller

import AssignmentDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import taegeuni.github.project_justrun.dto.AssignmentSubmitResponse
import taegeuni.github.project_justrun.dto.ErrorResponse
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

    @GetMapping("/assignments/{assignmentId}")
    fun getAssignment(
        @PathVariable assignmentId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<*> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val assignment = assignmentService.getAssignment(userId, assignmentId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse("Assignment not found"))

        val assignmentDto = AssignmentDto(
            assignmentId = assignment.assignmentId,
            title = assignment.title,
            content = assignment.content,
            attachment = assignment.attachment,
            attachmentName = assignment.attachmentName,
            dueDate = assignment.dueDate,
            courseId = assignment.course.courseId // Lazy Loading 방지
        )
        return ResponseEntity.ok(assignmentDto)
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    fun submitAssignment(
        @RequestParam("file") file: MultipartFile?,
        @RequestParam("content") content: String?,
        @PathVariable assignmentId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<*> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        assignmentService.submitAssignment(file, content, assignmentId, userId)
        return ResponseEntity.ok(
            AssignmentSubmitResponse(
                "assignment successfully submitted"
            )
        )
    }
}
