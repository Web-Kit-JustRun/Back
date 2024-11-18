package taegeuni.github.project_justrun.entity

import jakarta.persistence.*

@Entity
@Table(name = "user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val userId: Int = 0,

    @Column(unique = true, nullable = false, length = 50)
    val username: String,

    @Column(nullable = false, length = 255)
    var password: String, // 비밀번호 변경을 위해 var로 설정

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
    var rankingPoints: Int = 0, // 랭킹 포인트 업데이트를 위해 var로 설정

    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    var rewardPoints: Int = 0
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as User
        return userId == other.userId
    }

    override fun hashCode(): Int {
        return userId.hashCode()
    }
}

enum class UserType {
    student, // 소문자로 정의
    professor // 소문자로 정의
}
