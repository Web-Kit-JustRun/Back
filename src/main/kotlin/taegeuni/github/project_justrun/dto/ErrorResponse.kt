package taegeuni.github.project_justrun.dto

data class ErrorResponse(
    val message: String,
    val errors: List<String>? = null
)
