package ovh.snet.grzybek.aocslack

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class LeaderBoardService(
    private val leaderboardClient: LeaderboardClient, private val slackNotifier: SlackNotifier,
    @Value("\${aoc.slack.only-active-users:false}")
    private val onlyActiveUsers: Boolean,
) {

    @Scheduled(cron = "\${aoc.slack.leaderboard.cron:0 0 5 * * ?}", zone = "America/New_York")
    fun notifyCurrentLeaderBoard() {
        val newLeaderBoard = leaderboardClient.getLeaderBoard()
        var ranking = newLeaderBoard.getSortedMembersByLocalScore()

        if (onlyActiveUsers) {
            ranking = ranking.filter { it.localScore > 0 }
        }

        val randEmoji = listOf(":crown:", ":christmas_tree:", ":holiday_star:", ":blob_santa:", ":meow_santa:", ":medal:", ":tada:", ":snowman:", ":snowflake:")
        val emoji = randEmoji.random()

        val text = "$emoji *Leaderboard:*\n\n" + ranking.mapIndexed { index, member ->
            member.getMessage(index + 1)
        }.joinToString("\n")
        slackNotifier.sendSlackMessage(text)
    }
}