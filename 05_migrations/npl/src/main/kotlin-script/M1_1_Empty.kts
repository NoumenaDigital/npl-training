import com.noumenadigital.npl.migration.util.mapPrototypesInMigration
import com.noumenadigital.platform.migration.dsl.IdPair
import com.noumenadigital.platform.migration.dsl.migration

val fromVersion = "1.0"
val toVersion = "1.1"

val protos = mapPrototypesInMigration(
    overrideProtocols = listOf(
        IdPair("", "/npl-training-$toVersion?/board/Ballot"),
        IdPair("", "/npl-training-$toVersion?/board/Board"),
        IdPair("", "/npl-training-$toVersion?/board/Registration"),
        IdPair("", "/npl-training-$toVersion?/board/Tally"),
        IdPair("", "/npl-training-$toVersion?/board/Vote"),
    ),
    overrideStructs = listOf(
        IdPair("", "/npl-training-$toVersion?/board/VoteOutcome"),
    ),
    overrideEnums = listOf(
        IdPair("", "/npl-training-$toVersion?/board/VotingPossibility"),
    )
)

migration("NPL $fromVersion to $toVersion")
    .retag(protos)
