package taegeuni.github.project_justrun.dto

data class PasswordChangeRequest(
    val currentPassword: String,
    val newPassword: String
)