package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "quiz")
data class Quiz(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val quizId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: Course,

    @Column(nullable = false, length = 200)
    val title: String,

    @Column(nullable = false)
    val question: String,

    @Column(nullable = false, length = 200)
    val choice1: String,

    @Column(nullable = false, length = 200)
    val choice2: String,

    @Column(nullable = false, length = 200)
    val choice3: String,

    @Column(nullable = false, length = 200)
    val choice4: String,

    @Column(nullable = false)
    val correctChoice: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    val creator: User,

    @Column(nullable = false)
    val creationDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: QuizStatus = QuizStatus.pending,

    @Column(nullable = true)
    var points: Int? = null // 포인트 업데이트를 위해 var로 수정
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Quiz
        return quizId == other.quizId
    }

    override fun hashCode(): Int {
        return quizId.hashCode()
    }
}
enum class QuizStatus {
    pending,
    rejected,
    approved
}