package ovh.snet.grzybek.aocslack

import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
class AocSlackConfiguration {

    @Bean
    fun envVarLogger(logger: Logger): ApplicationListener<ApplicationReadyEvent> {
        return ApplicationListener {
            logger.log("=== Environment Variables ===", Logger.LogLevel.INFO)
            val envVars = System.getenv().entries
                .filter { it.key.startsWith("AOC_SLACK_") }
                .sortedBy { it.key }
            
            if (envVars.isEmpty()) {
                logger.log("No AOC_SLACK_* environment variables found", Logger.LogLevel.WARN)
            } else {
                envVars.forEach { (key, value) ->
                    logger.log("$key=$value", Logger.LogLevel.INFO)
                }
            }
            logger.log("=============================", Logger.LogLevel.INFO)
        }
    }
}