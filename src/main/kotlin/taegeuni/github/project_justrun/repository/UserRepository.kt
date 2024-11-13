package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType

interface UserRepository : JpaRepository<User, Int> {
    fun findByUsername(username: String): User?

    fun findAllByUserTypeOrderByRankingPointsDesc(userType: UserType): List<User>

    fun findTop10ByUserTypeOrderByRankingPointsDesc(userType: UserType = UserType.student): List<User>

    fun findTop100ByUserTypeOrderByRankingPointsDesc(userType: UserType = UserType.student): List<User>

    @Query("SELECT COUNT(u) + 1 FROM User u WHERE u.rankingPoints > :rankingPoints AND u.userType = 'student'")
    fun findUserRank(@Param("rankingPoints") rankingPoints: Int): Long
}
