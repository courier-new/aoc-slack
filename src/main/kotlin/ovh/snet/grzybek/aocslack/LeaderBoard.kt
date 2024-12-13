package ovh.snet.grzybek.aocslack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.beans.factory.annotation.Autowired
import kotlin.random.Random

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderBoard(
    val ownerId: Int,
    val event: String,
    val members: Map<String, Member>
) {
    fun findNewStars(
        newLeaderBoard: LeaderBoard,
        @Autowired logger: Logger,
        useTestData: Boolean = false
    ): List<Star> {
        logger.log("Looking for new stars", Logger.LogLevel.INFO)
        val newStars = mutableListOf<Star>()

        for ((memberId, newMember) in newLeaderBoard.members) {
            logger.log("Checking member $memberId", Logger.LogLevel.INFO)
            val oldMember = this.members[memberId] ?: continue

            if (useTestData) {
                logger.log("Using test data", Logger.LogLevel.INFO)
                val randomChance = Random.nextInt(3)
                if (randomChance == 0) {
                    logger.log("Randomly adding a new star for ${newMember.getMemberName()}", Logger.LogLevel.INFO)
                    newStars.add(Star(newMember.getMemberName(), Random.nextInt(1, 25), 1))
                }

                val randomThreeStarsChance = Random.nextInt(8)
                if (randomThreeStarsChance == 0) {
                    logger.log("Randomly adding three new stars for ${newMember.getMemberName()}", Logger.LogLevel.INFO)
                    newStars.add(Star(newMember.getMemberName(), Random.nextInt(1, 25), 2))
                    newStars.add(Star(newMember.getMemberName(), Random.nextInt(1, 25), 1))
                }
            } else {
                val newStarsForMember = findNewStarsForMember(newMember, oldMember, logger)
                newStars.addAll(newStarsForMember)
            }
        }

        return newStars
    }

    private fun findNewStarsForMember(
        newMember: Member,
        oldMember: Member,
        logger: Logger
    ): List<Star> {
        val newStars = mutableListOf<Star>()
        // Sort by days and iterate in order.
        newMember.completionDayLevel.keys.map { it.toInt() }.sorted().forEach { day ->
            val starsMap = newMember.completionDayLevel[day.toString()] ?: return@forEach
            // Sort by star number and iterate in order.
            starsMap.keys.map { it.toInt() }.sorted().forEach { star ->
                val oldLevel = oldMember.completionDayLevel[day.toString()]?.get(star.toString())
                if (oldLevel == null) {
                    logger.log("New star found for ${newMember.getMemberName()} on day $day", Logger.LogLevel.INFO)
                    newStars.add(Star(oldMember.getMemberName(), day, star))
                }
            }
        }
        return newStars
    }

    fun getSortedMembersByLocalScore(): List<Member> {
        return members.values.sortedWith(compareByDescending<Member> { it.localScore }.thenBy { it.getMemberName().toLowerCase() })
    }

    override fun toString(): String {
        val membersString = members.values.joinToString(separator = ", ") { member ->
            "${member.getMemberName()}: ${member.localScore}"
        }
        return "LeaderBoard(ownerId=$ownerId, event='$event', members=[$membersString])"
    }

    data class Member(
        val name: String?,
        val id: Int,
        val stars: Int,
        @JsonProperty("local_score")
        val localScore: Int,
        @JsonProperty("global_score")
        val globalScore: Int,
        @JsonProperty("last_star_ts")
        val lastStarTs: Long,
        @JsonProperty("completion_day_level")
        val completionDayLevel: Map<String, Map<String, Level>> = mapOf()
    ) {

        @JsonIgnoreProperties
        fun getMemberName(): String {
            if (name != null) {
                return name
            }
            return "Anonymous user #${id}"
        }

        fun getMessage(place: Int): String {
            val podium = when (place) {
                1 -> ":first_place_medal:"
                2 -> ":second_place_medal:"
                3 -> ":third_place_medal:"
                else -> ""
            }
            val placeSpacer = when (place) {
                in 1..9 -> "  "
                else -> ""
            }
            val scoreSpacer = when (localScore) {
                in 1000..9999 -> ""
                in 100..999 -> "  "
                in 10..99 -> "    "
                in 0..9 -> "      "
                else -> ""
            }
            val scoreCount = if (localScore == 1) "point" else "points"
            return "${placeSpacer}${place}. ${scoreSpacer}${localScore} ${scoreCount} - ${podium}${getMemberName()}"
        }


        data class Level(
            @JsonProperty("get_star_ts")
            val getStarTs: Long,
            @JsonProperty("star_index")
            val starIndex: Long
        )
    }

    data class Star(val member: String, val day: Int, val star: Int) {
        fun getMessage(): String {
            return "*${member}* received ${":star:".repeat(star)} for solving day ${day}'s challenge! :tada:"
        }
    }
}

