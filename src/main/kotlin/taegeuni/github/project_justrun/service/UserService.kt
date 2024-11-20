package taegeuni.github.project_justrun.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import taegeuni.github.project_justrun.entity.UserType
import taegeuni.github.project_justrun.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun getUserRanking(userId: Int): Map<String, Any> {
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }

        // 랭킹 포인트별로 정렬된 모든 학생을 내림차순으로 검색
        val students = userRepository.findAllByUserTypeOrderByRankingPointsDesc(UserType.student)
        val totalStudents = students.size

        // 사용자의 랭킹 위치 찾기
        val rankPosition = students.indexOfFirst { it.userId == userId } + 1

        // 랭킹 백분율 계산
        val rankingPercentage = (rankPosition.toDouble() / totalStudents)*100

        return mapOf(
            "user_id" to user.userId,
            "ranking_points" to user.rankingPoints,
            "ranking_percentage" to rankingPercentage
        )
    }

    //스토어 사이드바에서 자신의 리워드 포인트 확인
    fun getRewardPoints(userId: Int): Int {
        val user = userRepository.findById(userId).orElseThrow {
            EntityNotFoundException("User not found with id: $userId")
        }
        return user.rewardPoints
    }
}

