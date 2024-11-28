package ovh.snet.grzybek.aocslack

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import org.springframework.beans.factory.annotation.Autowired
import java.nio.file.Files
import java.nio.file.Paths

@Service
class LeaderboardClient(
    @Value("\${aoc.slack.session}")
    private val session: String,
    @Value("\${aoc.slack.year:#{T(java.time.Year).now().value}}")
    private val year: Int,
    @Value("\${aoc.slack.leaderboard-id}")
    private val leaderBoardId: Int,
    @Value("\${aoc.slack.use-test-data:false}")
    private val useTestData: Boolean,
    @Autowired private val logger: Logger
) {

    fun getLeaderBoard(): LeaderBoard {
        var leaderBoard: LeaderBoard = if (useTestData) {
            getLeaderBoardFromLocalFile()
        } else {
            getLeaderBoardFromWeb()
        }

        logger.log(leaderBoard.toString(), Logger.LogLevel.DEBUG)
        return leaderBoard
    }

    private fun getLeaderBoardFromWeb(): LeaderBoard {
        val client = WebClient.builder()
            .defaultHeader("Cookie", "session=${session}")
            .defaultHeader("User-Agent", "AoC Slack integration (kellirockwell@gmail.com) - https://github.com/courier-new/aoc-slack/tree/master")
            .build()

        logger.log("Fetching leaderboard", Logger.LogLevel.INFO)
        val result: Mono<LeaderBoard> = client.get()
            .uri("https://adventofcode.com/${year}/leaderboard/private/view/${leaderBoardId}.json")
            .retrieve()
            .bodyToMono(LeaderBoard::class.java)

        result.doOnError {
            logger.log("Error fetching leaderboard: ${it.message}", Logger.LogLevel.ERROR)
        }

        return result.block() ?: throw IllegalArgumentException("Leaderboard is empty")
    }

    private fun getLeaderBoardFromLocalFile(): LeaderBoard {
        logger.log("Reading leaderboard from file", Logger.LogLevel.INFO)
        val filePath = "leaderboard.json"
        val objectMapper = jacksonObjectMapper()
        val inputStream = javaClass.classLoader.getResourceAsStream(filePath)
            ?: throw IllegalArgumentException("File not found: $filePath")
        val json = inputStream.bufferedReader().use { it.readText() }
        return objectMapper.readValue(json)
    }
}
