package ovh.snet.grzybek.aocslack

import com.slack.api.Slack
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
class SlackNotifier(
    @Value("\${aoc.slack.webhook.url}")
    private val webhookUrl: String,
    @Value("\${aoc.slack.debug-mode:false}")
    private val debugMode: Boolean,
    @Autowired private val logger: Logger
) {
    private val slack: Slack = Slack.getInstance()

    fun sendSlackMessage(message: String) {
        val payload = "{\"text\":\"${message}\"}"
        if (!debugMode) {
            logger.log("Sending message to Slack: $message", Logger.LogLevel.INFO)
            slack.send(webhookUrl, payload)
        } else {
            logger.log("Debug mode enabled, skipping sending message to Slack: $message", Logger.LogLevel.INFO)
        }
    }
}