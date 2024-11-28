package ovh.snet.grzybek.aocslack

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.beans.factory.annotation.Autowired

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class LeaderBoard(
    val ownerId: Int,
    val event: String,
    val members: Map<String, Member>
) {
    fun findNewStars(
        newLeaderBoard: LeaderBoard,
        @Autowired logger: Logger
    ): List<Star> {
        logger.log("Looking for new stars", Logger.LogLevel.INFO)
        val newStars = mutableListOf<Star>()

        for ((memberId, newMember) in newLeaderBoard.members) {
            logger.log("Checking member $memberId", Logger.LogLevel.INFO)
            val oldMember = this.members[memberId] ?: continue
            newStars.addAll(addStars(newMember, oldMember, logger))
        }

        return newStars
    }

    private fun addStars(
        newMember: Member,
        oldMember: Member,
        logger: Logger
    ): List<Star> {
        val newStars = mutableListOf<Star>()
        newMember.completionDayLevel.forEach { (day, starsMap) ->
            starsMap.forEach { (star, _) ->
                val oldLevel = oldMember.completionDayLevel[day]?.get(star)
                if (oldLevel == null) {
                    logger.log("New star found for ${newMember.getMemberName()} on day $day", Logger.LogLevel.INFO)
                    newStars.add(Star(oldMember.getMemberName(), day.toInt(), star.toInt()))
                }
            }
        }
        return newStars
    }

    fun getSortedMembersByLocalScore(): List<Member> {
        return members.values.sortedWith(compareByDescending<Member> { it.localScore }.thenBy { it.getMemberName() })
    }
    }

    data class Member(
        val name: String?,
        val id: Int,
        val stars: Int,
        @JsonProperty("local_score")
        val localScore: Int,
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
                1 -> ":first_place_medal: "
                2 -> ":second_place_medal: "
                3 -> ":third_place_medal: "
                else -> ""
            }
            return "${place}. ${podium}*${getMemberName()}* ${localScore}"
        }


        data class Level(val getStarTs: Long, val starIndex: Long)
    }

    data class Star(val member: String, val day: Int, val star: Int) {
        fun getMessage(): String {
            return "*${member}* received ${":star:".repeat(star)} for solving day ${day}'s challenge :tada:\n"
        }
    }
}

