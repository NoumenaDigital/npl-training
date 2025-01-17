import com.noumenadigital.platform.engine.testing.EngineMigrationTester
import com.noumenadigital.platform.engine.testing.MigrationInMemoryTestConfiguration
import io.kotest.core.spec.style.FunSpec
import java.nio.file.Path
import java.nio.file.Paths

class MigrationTest : FunSpec({

    val fromVersion = "v1"
    val toVersion = "v2"

    fun getFilePath(filePath: String): Path = checkNotNull(Paths.get("src/solution/$filePath")).toAbsolutePath()

    fun createDefaultTester() =
        EngineMigrationTester(
            getFilePath("main").toFile(),
            MigrationInMemoryTestConfiguration(party = "borrower"),
        )

    test("npl $fromVersion to $toVersion In-memory") {
        val engine = createDefaultTester()
        engine.runTo(fromVersion)
        engine.runNPL(getFilePath("test/migration/npl/npl-run-$fromVersion.npl"))

        engine.runTo(toVersion)
        engine.runNPL(getFilePath("test/migration/npl/npl-run-$toVersion.npl"))
    }
})
