package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import taegeuni.github.project_justrun.entity.User

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
}
