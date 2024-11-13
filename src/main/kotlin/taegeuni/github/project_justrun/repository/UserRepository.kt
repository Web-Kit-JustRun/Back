package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?
    fun findAllByUserTypeOrderByRankingPointsDesc(userType: UserType): List<User>
}

