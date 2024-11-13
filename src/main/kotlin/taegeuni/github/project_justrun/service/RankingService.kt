package taegeuni.github.project_justrun.service

import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.entity.User
import taegeuni.github.project_justrun.entity.UserType
import taegeuni.github.project_justrun.repository.UserRepository

@Service
class RankingService(
    private val userRepository: UserRepository
) {

    fun getTop10Ranking(): List<Map<String, Any>> {
        val top10Users = userRepository.findTop10ByUserTypeOrderByRankingPointsDesc()
        return top10Users.mapIndexed { index, user ->
            mapOf(
                "rank" to index + 1,
                "user_id" to user.userId,
                "name" to user.name,
                "ranking_points" to user.rankingPoints
            )
        }
    }

    // 상위 100명 학생 조회
    fun getTop100Students(): List<Map<String, Any>> {
        return userRepository.findTop100ByUserTypeOrderByRankingPointsDesc(UserType.student)
            .mapIndexed { index, student ->
                mapOf(
                    "rank" to index + 1,
                    "user_id" to student.userId,
                    "name" to student.name,
                    "ranking_points" to student.rankingPoints
                )
            }
    }

    // 사용자 순위 조회
    fun getUserRank(user: User): Map<String, Any> {
        val userRank = userRepository.findUserRank(user.rankingPoints)
        return mapOf(
            "rank" to userRank,
            "user_id" to user.userId,
            "name" to user.name,
            "ranking_points" to user.rankingPoints
        )
    }
}
