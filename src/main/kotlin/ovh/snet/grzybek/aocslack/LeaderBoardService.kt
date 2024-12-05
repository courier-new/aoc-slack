package ovh.snet.grzybek.aocslack

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LeaderBoardService(
    private val leaderboardClient: LeaderboardClient, private val slackNotifier: SlackNotifier,
    @Value("\${aoc.slack.only-active-users:false}")
    private val onlyActiveUsers: Boolean,
    @Autowired private val logger: Logger
) {

    @Scheduled(cron = "\${aoc.slack.leaderboard.cron:15 5 0 1-25 12 ?}", zone = "America/New_York")
    fun notifyCurrentLeaderBoard() {
        logger.log("STARTING LEADERBOARD REFRESH JOB", Logger.LogLevel.INFO)
        val newLeaderBoard = leaderboardClient.getLeaderBoard()
        var ranking = newLeaderBoard.getSortedMembersByLocalScore()

        if (onlyActiveUsers) {
            ranking = ranking.filter { it.localScore > 0 }
        }

        val text = "It's time for a leaderboard update!\n\n" + ranking.mapIndexed { index, member ->
            member.getMessage(index + 1)
        }.joinToString("\n")
        slackNotifier.sendSlackMessage(text)
    }
}