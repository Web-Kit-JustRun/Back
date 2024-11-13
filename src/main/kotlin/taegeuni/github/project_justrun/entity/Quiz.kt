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
    val status: QuizStatus = QuizStatus.pending
)
enum class QuizStatus {
    pending,
    rejected,
    approved
}