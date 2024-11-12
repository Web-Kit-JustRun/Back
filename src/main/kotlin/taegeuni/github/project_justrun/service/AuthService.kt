package taegeuni.github.project_justrun.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taegeuni.github.project_justrun.exception.UnauthorizedException
import taegeuni.github.project_justrun.repository.UserRepository

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    @Transactional
    fun changePassword(userId: Int, currentPassword: String, newPassword: String) {
        val user = userRepository.findById(userId).orElseThrow {
            throw IllegalArgumentException("User not found")
        }

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            throw UnauthorizedException("Incorrect current password")
        }

        // 새 비밀번호 설정 및 저장
        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
    }
}
