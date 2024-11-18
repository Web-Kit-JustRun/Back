package taegeuni.github.project_justrun.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class QuizAttemptRequest(
    @JsonProperty("selected_choice")
    val selectedChoice: Int
)
