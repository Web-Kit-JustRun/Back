package taegeuni.github.project_justrun

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ProjectJustrunApplication

fun main(args: Array<String>) {
    runApplication<ProjectJustrunApplication>(*args)
}
