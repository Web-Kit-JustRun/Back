package taegeuni.github.project_justrun.dto

data class QuizRequest(
    val title: String,
    val question: String,
    val choices: List<String>,
    val correctChoice: Int
)
