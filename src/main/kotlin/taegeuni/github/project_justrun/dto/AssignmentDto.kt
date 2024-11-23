import java.time.LocalDateTime

data class AssignmentDto(
    val assignmentId: Int,
    val title: String,
    val content: String,
    val attachment: String?,
    val attachmentName: String?,
    val dueDate: LocalDateTime,
    val courseId: Int
)