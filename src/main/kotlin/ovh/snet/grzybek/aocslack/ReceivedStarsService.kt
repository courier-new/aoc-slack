package ovh.snet.grzybek.aocslack

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.beans.factory.annotation.Autowired

@Service
class ReceivedStarsService(
    private val leaderboardClient: LeaderboardClient,
    private val slackNotifier: SlackNotifier,
    @Autowired private val logger: Logger
) {

    private var leaderBoard: LeaderBoard = leaderboardClient.getLeaderBoard()

    @Scheduled(cron = "\${aoc.slack.stars.cron:0 0/15 * * * ?}", zone = "America/New_York")
    fun notifyReceivedStarts() {
        val newLeaderBoard = leaderboardClient.getLeaderBoard()
        val newStars = leaderBoard.findNewStars(newLeaderBoard, logger)
        notifySlack(newStars)
        leaderBoard = newLeaderBoard
    }

    fun notifySlack(stars: List<LeaderBoard.Star>) {
        val text = stars.joinToString("\n") { it.getMessage() }
        slackNotifier.sendSlackMessage(text)
    }
}