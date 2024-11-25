package taegeuni.github.project_justrun.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*

@Service
class FileStorageService(
    @Value("\${file.upload-dir}")
    private val uploadDir: String
) {

    private val assignmentDir: File

    init {
        // 파일 저장 경로를 설정합니다.
        assignmentDir = Paths.get(uploadDir).toAbsolutePath().normalize().toFile()

        // 디렉토리가 존재하지 않으면 생성합니다.
        if (!assignmentDir.exists()) {
            assignmentDir.mkdirs()
        }
    }

    @Throws(IOException::class)
    fun storeAssignmentFile(file: MultipartFile): String {
        val originalFilename = StringUtils.cleanPath(file.originalFilename ?: "unknown")
        val fileName = UUID.randomUUID().toString() + "_" + originalFilename

        val targetLocation = File(assignmentDir, fileName)

        // 파일 저장
        file.inputStream.use { inputStream ->
            Files.copy(inputStream, targetLocation.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        return targetLocation.absolutePath // 저장된 파일의 절대 경로를 반환합니다.
    }
    //파일 삭제
    fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }
}
