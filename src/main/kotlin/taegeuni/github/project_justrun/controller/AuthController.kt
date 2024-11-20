package taegeuni.github.project_justrun.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import taegeuni.github.project_justrun.dto.CommonResponse
import taegeuni.github.project_justrun.dto.ErrorResponse
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType
import taegeuni.github.project_justrun.repository.UserRepository
import taegeuni.github.project_justrun.util.JwtUtil
import java.time.LocalDate

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    @PostMapping("/register")
    fun register(@RequestBody body: RegisterRequestDto): ResponseEntity<*> {
        val user = userRepository.findByUsernameOrEmail(body.username, body.email)
        if (user != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponse("User already exists"))
        }

        val latestStudent = userRepository.findTopByStudentNumberIsNotNullAndUserTypeOrderByUserIdDesc()
        val studentNumber = LocalDate.now().year.toString() + String.format("%04d", latestStudent?.studentNumber?.drop(4)?.toInt()?.plus(1) ?: 1)

        val newUser = User(
            username = body.username,
            email = body.email,
            password = passwordEncoder.encode(body.password),
            name = body.name,
            userType = UserType.student,
            studentNumber = studentNumber
        )
        userRepository.save(newUser)

        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse("User registered successfully"))
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        val user = userRepository.findByUsername(request.username)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse("Invalid username or password"))

        return if (passwordEncoder.matches(request.password, user.password)) {
            val token = jwtUtil.generateToken(user.userId) // 수정된 부분
            val response = LoginResponse(
                message = "Login successful.",
                token = token,
                user = UserDto(
                    userId = user.userId,
                    username = user.username,
                    name = user.name,
                    userType = user.userType.name.lowercase(),
                    rankingPoints = user.rankingPoints,
                    rewardPoints = user.rewardPoints
                )
            )
            ResponseEntity.ok(response)
        } else {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse("Invalid username or password"))
        }
    }
}

data class LoginRequest(val username: String, val password: String)

data class RegisterRequestDto(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
)

data class LoginResponse(
    val message: String,
    val token: String,
    val user: UserDto
)

data class UserDto(
    val userId: Int,
    val username: String,
    val name: String,
    val userType: String,
    val rankingPoints: Int,
    val rewardPoints: Int
)
