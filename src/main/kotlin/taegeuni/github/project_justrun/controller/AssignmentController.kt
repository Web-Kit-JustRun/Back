package taegeuni.github.project_justrun.controller

import AssignmentDto
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import taegeuni.github.project_justrun.dto.AssignmentCreateRequest
import taegeuni.github.project_justrun.dto.AssignmentCreateResponse
import taegeuni.github.project_justrun.dto.AssignmentSubmitResponse
import taegeuni.github.project_justrun.dto.ErrorResponse
import taegeuni.github.project_justrun.service.AssignmentService
import taegeuni.github.project_justrun.util.JwtUtil
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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

    //교수가 과제 생성
    @PostMapping("/courses/{courseId}/assignments", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createAssignment(
        @PathVariable courseId: Int,
        @RequestHeader("Authorization") token: String,
        @RequestParam("title") title: String,
        @RequestParam("content") content: String,
        @RequestParam("maxScore") maxScore: Int,
        @RequestParam("dueDate") dueDateStr: String,
        @RequestParam("attachment", required = false) attachment: MultipartFile?
    ): ResponseEntity<AssignmentCreateResponse> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))

        // dueDate 파싱
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val dueDate = try {
            LocalDateTime.parse(dueDateStr, formatter)
        } catch (e: DateTimeParseException) {
            throw IllegalArgumentException("dueDate 형식이 잘못되었습니다. 'YYYY-MM-DDTHH:MM:SS' 형식이어야 합니다.")
        }

        val request = AssignmentCreateRequest(
            title = title,
            content = content,
            maxScore = maxScore,
            dueDate = dueDate
        )

        val response = assignmentService.createAssignment(userId, courseId, request, attachment)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
