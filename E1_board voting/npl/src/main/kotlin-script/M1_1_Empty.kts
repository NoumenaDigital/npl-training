import com.noumenadigital.npl.migration.util.mapPrototypesInMigration
import com.noumenadigital.platform.migration.dsl.IdPair
import com.noumenadigital.platform.migration.dsl.migration

val fromVersion = "1.0"
val toVersion = "1.1"

val protos = mapPrototypesInMigration(
    overrideProtocols = listOf(
        IdPair("", "/library-$toVersion?/board/Ballot"),
        IdPair("", "/library-$toVersion?/board/Board"),
        IdPair("", "/library-$toVersion?/board/Registration"),
        IdPair("", "/library-$toVersion?/board/Tally"),
        IdPair("", "/library-$toVersion?/board/Vote"),
    ),
    overrideStructs = listOf(
        IdPair("", "/library-$toVersion?/board/VoteOutcome"),
    ),
    overrideEnums = listOf(
        IdPair("", "/library-$toVersion?/board/VotingPossibility"),
    )
)

migration("NPL $fromVersion to $toVersion")
    .retag(protos)
