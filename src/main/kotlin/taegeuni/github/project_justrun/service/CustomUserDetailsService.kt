package taegeuni.github.project_justrun.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.repository.UserRepository

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findById(username.toInt())
            .orElseThrow { UsernameNotFoundException("User not found with id: $username") }
        return org.springframework.security.core.userdetails.User(
            user.userId.toString(),
            user.password,
            listOf() // 권한 추가 필요 시 여기에 추가
        )
    }
}
