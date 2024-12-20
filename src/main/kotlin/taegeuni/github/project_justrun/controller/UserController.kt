package taegeuni.github.project_justrun.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.dto.PasswordChangeRequest
import taegeuni.github.project_justrun.dto.PurchaseHistoryResponseItem
import taegeuni.github.project_justrun.dto.StudentNumberResponse
import taegeuni.github.project_justrun.dto.UserQuizResponseItem
import taegeuni.github.project_justrun.exception.ForbiddenException
import taegeuni.github.project_justrun.service.*
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api/users")
class UserController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil,
    private val userService: UserService,
    private val storeItemService: StoreItemService,
    private val courseService: CourseService,
    private val quizService: QuizService,
) {

    @PutMapping("/{userId}/change-password")
    fun changePassword(
        @PathVariable userId: Int,
        @RequestBody request: PasswordChangeRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        // Authorization 헤더에서 JWT 토큰 추출
        val authHeader = httpRequest.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ForbiddenException("Authorization header missing or malformed.")
        }
        val token = authHeader.substring(7)

        // JwtUtil을 사용하여 JWT에서 userId 추출
        val jwtUserId = jwtUtil.getUserIdFromToken(token)
        println("JWT userId: $jwtUserId, URL userId: $userId")

        // URL의 userId와 JWT 토큰의 userId가 일치하는지 검증
        if (userId != jwtUserId) {
            throw ForbiddenException("You do not have permission to change another user's password.")
        }

        // 비밀번호 변경 로직 실행
        authService.changePassword(userId, request.currentPassword, request.newPassword)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully."))
    }

    @GetMapping("/{userId}/ranking")
    fun getUserRanking(
        @PathVariable userId: Int,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        val authHeader = httpRequest.getHeader("Authorization")
        val token = authHeader.substring(7)
        val jwtUserId = jwtUtil.getUserIdFromToken(token)

        if (userId != jwtUserId) {
            return ResponseEntity.status(403).body("You do not have permission to access this ranking data.")
        }

        val rankingData = userService.getUserRanking(userId)
        return ResponseEntity.ok(rankingData)
    }

    @GetMapping("/{userId}/courses")
    fun getUserCourses(
        @PathVariable userId: Int,
        httpRequest: HttpServletRequest
    ): ResponseEntity<List<Map<String, Any>>> {
        // 여기에 인증 및 권한 검증 코드 추가

        val courses = courseService.getCoursesForUser(userId)
        return ResponseEntity.ok(courses)
    }

    //구매내역 조회
    @GetMapping("/{userId}/purchases")
    fun getPurchaseHistory(
        @PathVariable userId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<PurchaseHistoryResponseItem>> {
        val requestingUserId = jwtUtil.getUserIdFromToken(token.substring(7))

        val purchaseHistory = storeItemService.getPurchaseHistory(requestingUserId, userId)
        return ResponseEntity.ok(purchaseHistory)
    }

    //스토어 사이드바에서 자신의 리워드 포인트 확인
    @GetMapping("/reward_point")
    fun getRewardPoint(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<Map<String, Int>> {
        val userId = userDetails.username.toInt()
        val rewardPoint = userService.getRewardPoints(userId)
        return ResponseEntity.ok(mapOf("rewardPoint" to rewardPoint))
    }

    //자신의 퀴즈 확인
    @GetMapping("/{userId}/quizzes")
    fun getUserQuizzes(
        @PathVariable userId: Int,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<List<UserQuizResponseItem>> {
        val requestingUserId = jwtUtil.getUserIdFromToken(token.substring(7))
        val userQuizzes = quizService.getUserQuizzes(requestingUserId, userId)
        return ResponseEntity.ok(userQuizzes)
    }

    //내 학번 조회
    @GetMapping("/profile")
    fun getStudentNumber(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<StudentNumberResponse> {
        // JWT 토큰에서 사용자 ID 추출
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))

        // 서비스 메서드 호출
        val response = userService.getStudentNumber(userId)

        return ResponseEntity.ok(response)
    }
}
