package ovh.snet.grzybek.aocslack

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class Logger(
    @Value("\${aoc.slack.debug-mode:false}")
    private val isDebugEnabled: Boolean
) {
    enum class LogLevel {
        DEBUG, INFO, WARN, ERROR
    }

    private val RESET = "\u001B[0m"
    private val INFO_COLOR = "\u001B[32m"  // Green
    private val WARN_COLOR = "\u001B[33m"  // Yellow
    private val ERROR_COLOR = "\u001B[31m" // Red

    fun log(message: String, level: LogLevel) {
        val formattedMessage = when (level) {
            LogLevel.DEBUG -> "[DEBUG] $message"
            LogLevel.INFO -> "${INFO_COLOR}[INFO] $message${RESET}"
            LogLevel.WARN -> "${WARN_COLOR}[WARNING] $message${RESET}"
            LogLevel.ERROR -> "${ERROR_COLOR}[ERROR] $message${RESET}"
        }

        if (isDebugEnabled || level != LogLevel.DEBUG) {
            println(formattedMessage)
        }
    }
}