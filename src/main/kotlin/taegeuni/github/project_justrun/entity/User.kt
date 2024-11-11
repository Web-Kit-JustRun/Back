package taegeuni.github.project_justrun.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Int = 0,

    @Column(unique = true, nullable = false, length = 50)
    val username: String,

    @Column(nullable = false, length = 255)
    val password: String,

    @Column(unique = true, nullable = false, length = 100)
    val email: String,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = true, length = 20)
    val studentNumber: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val userType: UserType,

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    val rankingPoints: Int = 0,

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    val rewardPoints: Int = 0
)

enum class UserType {
    student, // 소문자로 정의
    professor // 소문자로 정의
}

