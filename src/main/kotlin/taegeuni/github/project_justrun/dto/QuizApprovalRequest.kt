package taegeuni.github.project_justrun.dto

data class QuizApprovalRequest(
    val isApprove: String, // "approve" 또는 "reject"

    val points: Int? = null // approve일 때 필수, reject일 때는 null 또는 생략 가능
)
