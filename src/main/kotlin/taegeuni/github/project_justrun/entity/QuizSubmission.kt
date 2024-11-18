package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "quiz_submission",
    uniqueConstraints = [UniqueConstraint(columnNames = ["quiz_id", "student_id"])]
)
data class QuizSubmission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val submissionId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    val quiz: Quiz,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: User,

    @Column(nullable = false)
    var selectedChoice: Int,

    @Column(nullable = false)
    var isCorrect: Boolean,

    @Column(nullable = false)
    var submissionDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var pointsAwarded: Boolean = false
)
