package taegeuni.github.project_justrun.entity

import jakarta.persistence.*

@Entity
@Table(name = "course")
data class Course(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val courseId: Int = 0,

    @Column(nullable = false, length = 100)
    val courseName: String,

    @Column(columnDefinition = "TEXT")
    val courseDescription: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    val professor: User
)

