package taegeuni.github.project_justrun.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil(
    @Value("\${jwt.secret}") private val secretKey: String,
    @Value("\${jwt.expirationTime}") private val expirationTime: Long
) {

    fun generateToken(userId: Int): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationTime) // 만료 시간을 속성에서 가져옴

        return Jwts.builder()
            .setSubject(userId.toString()) // userId를 sub 필드에 저장
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()), SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(secretKey.toByteArray()).build().parseClaimsJws(token)
            true
        } catch (ex: Exception) {
            println("Invalid JWT token: ${ex.message}")  // 유효성 검사 실패 원인 로그
            false
        }
    }

    fun getUserIdFromToken(token: String): Int {
        val claims = Jwts.parserBuilder()
            .setSigningKey(secretKey.toByteArray())
            .build()
            .parseClaimsJws(token)
            .body
        println("Extracted userId from token: ${claims.subject}")  // 디버그용 출력
        return claims.subject.toInt()
    }

}
