import com.noumenadigital.npl.lang.TextValue
import com.noumenadigital.npl.migration.util.mapPrototypesInMigration
import com.noumenadigital.platform.migration.dsl.IdPair
import com.noumenadigital.platform.migration.dsl.migration

val systemUnderAudit = "npltraining"
val fromVersion = "v1"
val toVersion = "v2"
val packageName = "crowdLending_solution05" // To be adjusted if copied to the exercise folder

val prototypes = mapPrototypesInMigration(
    overrideStructs = listOf(
        IdPair(
            current = "",
            target = "/$systemUnderAudit-$toVersion?/$packageName/Topic"
        )
    )
)

migration("${prototypes.current} to ${prototypes.target}")
    .transformProtocol(
        "/$systemUnderAudit-$fromVersion?/$packageName/LoanRequest",
        "/$systemUnderAudit-$toVersion?/$packageName/LoanRequest"
    ) {
        val topic = get<TextValue>("topic")
        val topicStruct = createStruct(
            "/$systemUnderAudit-$toVersion?/$packageName/Topic",
            mapOf(
                "title" to topic,
                "description" to createText(""),
                "author" to createText(""),
            )
        )
        delete("topic")
        put("topic") { topicStruct }
    }
    .retag(prototypes)
