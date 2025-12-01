package ovh.snet.grzybek.aocslack

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AocSlackApplication

fun main(args: Array<String>) {
    // Log environment variables before Spring Boot starts
    println("\u001B[32m[INFO] === Environment Variables ===\u001B[0m")
    val envVars = System.getenv().entries
        .filter { it.key.startsWith("AOC_SLACK_") }
        .sortedBy { it.key }
    
    if (envVars.isEmpty()) {
        println("\u001B[33m[WARNING] No AOC_SLACK_* environment variables found\u001B[0m")
    } else {
        envVars.forEach { (key, value) ->
            println("\u001B[32m[INFO] $key=$value\u001B[0m")
        }
    }
    println("\u001B[32m[INFO] ============================\u001B[0m")
    
    runApplication<AocSlackApplication>(*args)
}
