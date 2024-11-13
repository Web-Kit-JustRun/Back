package taegeuni.github.project_justrun.entity

import jakarta.persistence.*

@Entity
@Table(name = "enrollment")
data class Enrollment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val enrollmentId: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    val course: Course
)
