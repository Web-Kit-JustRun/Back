package taegeuni.github.project_justrun.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import taegeuni.github.project_justrun.dto.StudentNumberResponse
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
            "userId" to user.userId,
            "rankingPoints" to user.rankingPoints,
            "rankingPercentage" to rankingPercentage
        )
    }

    //스토어 사이드바에서 자신의 리워드 포인트 확인
    fun getRewardPoints(userId: Int): Int {
        val user = userRepository.findById(userId).orElseThrow {
            EntityNotFoundException("User not found with id: $userId")
        }
        return user.rewardPoints
    }

    //내 학번 조회
    @Transactional(readOnly = true)
    fun getStudentNumber(userId: Int): StudentNumberResponse {
        // 사용자 조회
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("사용자를 찾을 수 없습니다.") }

        // 학생인지 확인
        if (user.userType != UserType.student) {
            throw IllegalAccessException("학생만 학번을 조회할 수 있습니다.")
        }

        // 학번이 있는지 확인 및 로컬 변수에 할당
        val studentNumber = user.studentNumber
            ?: throw NoSuchElementException("학번 정보가 없습니다.")

        // 응답 생성
        return StudentNumberResponse(
            userId = user.userId,
            studentNumber = studentNumber
        )
    }
}

