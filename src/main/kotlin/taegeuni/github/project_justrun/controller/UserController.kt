package taegeuni.github.project_justrun.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import taegeuni.github.project_justrun.dto.PasswordChangeRequest
import taegeuni.github.project_justrun.exception.ForbiddenException
import taegeuni.github.project_justrun.service.AuthService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api/users")
class UserController(
    private val authService: AuthService,
    private val jwtUtil: JwtUtil
) {

    @PutMapping("/{userId}/change-password")
    fun changePassword(
        @PathVariable userId: Int,  // URL에서 전달된 userId
        @RequestBody request: PasswordChangeRequest,
        httpRequest: HttpServletRequest
    ): ResponseEntity<Any> {
        // 1. Authorization 헤더에서 JWT 토큰 추출
        val authHeader = httpRequest.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw ForbiddenException("Authorization header missing or malformed.")
        }
        val token = authHeader.substring(7) // "Bearer " 이후의 토큰 부분만 추출

        // 2. JwtUtil을 사용하여 JWT에서 userId 추출
        val jwtUserId = jwtUtil.getUserIdFromToken(token)
        println("JWT userId: $jwtUserId, URL userId: $userId") // 디버깅을 위한 출력

        // 3. URL의 userId와 JWT 토큰의 userId가 일치하는지 검증
        if (userId != jwtUserId) {
            throw ForbiddenException("You do not have permission to change another user's password.")
        }

        // 비밀번호 변경 로직 실행
        authService.changePassword(userId, request.currentPassword, request.newPassword)
        return ResponseEntity.ok(mapOf("message" to "Password changed successfully."))
    }

}
