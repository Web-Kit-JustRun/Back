package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assignment_submission", uniqueConstraints = [
    UniqueConstraint(columnNames = ["assignment_id", "student_id"])
])
data class AssignmentSubmission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val submissionId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    val assignment: Assignment,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    val student: User,

    @Column(nullable = true)
    val content: String? = null,

    @Column(nullable = true, length = 255)
    val attachment: String? = null,

    @Column(nullable = true, length = 200)
    val attachmentName: String? = null,

    @Column(nullable = false)
    val submissionDate: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    val score: Int? = null,

    @Column(nullable = false)
    val isScored: Boolean = false,

    @Column(nullable = false)
    val isRewardGiven: Boolean = false
)
