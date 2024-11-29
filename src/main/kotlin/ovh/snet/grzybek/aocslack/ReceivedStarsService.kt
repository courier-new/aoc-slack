package ovh.snet.grzybek.aocslack

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value

@Service
class ReceivedStarsService(
    private val leaderboardClient: LeaderboardClient,
    private val slackNotifier: SlackNotifier,
    @Value("\${aoc.slack.use-test-data:false}") private val useTestData: Boolean,
    @Autowired private val logger: Logger
) {

    private var leaderBoard: LeaderBoard = leaderboardClient.getLeaderBoard()

    @Scheduled(cron = "\${aoc.slack.stars.cron:0 0/15 * * * ?}", zone = "America/New_York")
    fun notifyReceivedStars() {
        logger.log("STARTING RECEIVED STARS JOB", Logger.LogLevel.INFO)
        val newLeaderBoard = leaderboardClient.getLeaderBoard()
        val newStars = leaderBoard.findNewStars(newLeaderBoard, logger, useTestData)
        if (newStars.isNotEmpty()) {
            notifySlack(newStars)
        } else {
            logger.log("No new stars found", Logger.LogLevel.INFO)
        }
        leaderBoard = newLeaderBoard
    }

    fun notifySlack(stars: List<LeaderBoard.Star>) {
        val text = stars.joinToString("\n\n") { it.getMessage() }
        slackNotifier.sendSlackMessage(text)
    }
}