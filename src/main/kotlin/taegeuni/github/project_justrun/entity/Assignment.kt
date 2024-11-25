package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "assignment")
data class Assignment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val assignmentId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: Course,

    @Column(nullable = false, length = 200)
    val title: String,

    @Column(nullable = false)
    val content: String,

    @Column(nullable = true, length = 255)
    val attachment: String? = null,

    @Column(nullable = true, length = 200)
    val attachmentName: String? = null,

    @Column(nullable = false)
    val dueDate: LocalDateTime,

    @Column(nullable = false)
    val maxScore: Int, // 만점 점수 추가

    @Column(nullable = false)
    val creationDate: LocalDateTime = LocalDateTime.now()
)

