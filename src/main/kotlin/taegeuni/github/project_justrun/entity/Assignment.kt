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
    var title: String,

    @Column(nullable = false)
    var content: String,

    @Column(nullable = true, length = 255)
    var attachment: String? = null,

    @Column(nullable = true, length = 200)
    var attachmentName: String? = null,

    @Column(nullable = false)
    var dueDate: LocalDateTime,

    @Column(nullable = false)
    var maxScore: Int, // 만점 점수 추가

    @Column(nullable = false)
    val creationDate: LocalDateTime = LocalDateTime.now()
)

