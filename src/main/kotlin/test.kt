import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

fun main() {
    val passwordEncoder = BCryptPasswordEncoder()
//    val encodedPassword = passwordEncoder.encode("testprof123")  // 원하는 비밀번호로 설정
//    println(encodedPassword)
    for (i in 1..10) {
        val encodedPassword = passwordEncoder.encode("professor$i")
        println(encodedPassword)
    }

}