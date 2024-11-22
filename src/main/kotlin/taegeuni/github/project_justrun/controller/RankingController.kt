package taegeuni.github.project_justrun.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import taegeuni.github.project_justrun.repository.UserRepository
import taegeuni.github.project_justrun.service.RankingService
import taegeuni.github.project_justrun.util.JwtUtil

@RestController
@RequestMapping("/api/ranking")
class RankingController(
    private val rankingService: RankingService,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil  // jwtUtil 주입 추가
) {

    @GetMapping("/top")
    fun getTop10Ranking(): ResponseEntity<List<Map<String, Any>>> {
        val top10Ranking = rankingService.getTop10Ranking()
        return ResponseEntity.ok(top10Ranking)
    }

    // 상위 100명 및 사용자 랭킹 조회
    @GetMapping
    fun getTop100AndUserRank(
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<RankingData> {
        val userId = jwtUtil.getUserIdFromToken(token.substring(7))
        val user = userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }

        val top100 = rankingService.getTop100Students()
        val userRankData = rankingService.getUserRank(user)

        val response = RankingData(top100, userRankData)
        return ResponseEntity.ok(response)
    }
}

data class RankingData(
    val top100: List<Map<String, Any>>,
    val userRanking: Map<String, Any>
)