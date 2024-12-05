package ovh.snet.grzybek.aocslack

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDate

@Service
class DailyPostService(
    private val slackNotifier: SlackNotifier,
    @Autowired private val logger: Logger
) {
    @Scheduled(cron = "\${aoc.slack.daily-post.cron:0 0 0 1-25 12 ?}", zone = "America/New_York")
    fun notifySlack() {
        logger.log("STARTING DAILY POST JOB", Logger.LogLevel.INFO)
        val (year, day) = LocalDate.now().let { it.year to it.dayOfMonth }

        val text = ":thread: Chatter for <https://adventofcode.com/$year/day/$day|Day $day>. Spoilers inside."

        slackNotifier.sendSlackMessage(text)
    }
}