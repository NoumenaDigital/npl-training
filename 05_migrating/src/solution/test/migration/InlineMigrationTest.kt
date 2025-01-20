import com.noumenadigital.platform.engine.testing.EngineMigrationTester
import com.noumenadigital.platform.engine.testing.MigrationInMemoryTestConfiguration
import io.kotest.core.spec.style.FunSpec
import java.nio.file.Path
import java.nio.file.Paths

class InlineMigrationTest : FunSpec({

    val fromVersion = "v1"
    val toVersion = "v2"

    fun getFilePath(filePath: String): Path = checkNotNull(
        Paths.get("src/solution/$filePath")
    ).toAbsolutePath()

    fun createDefaultTester() =
        EngineMigrationTester(
            getFilePath("main").toFile(),
            MigrationInMemoryTestConfiguration(party = "borrower"),
        )

    test("npl $fromVersion to $toVersion") {
        val engine = createDefaultTester()
        engine.runTo(fromVersion)

        val topic = "my loan request"

        engine.runNPL(
            name = testCase.name.testName.plus("-$fromVersion"),
            snippet = """
                package test;
                use crowdLending_solution05.LoanRequest;
                
                function getAliceParty() returns Party -> {
                    return partyOf(
                        mapOf(Pair("email", setOf("alice@nd.tech"))),
                        mapOf<Text, Set<Text>>()
                    );
                };

                function getBobParty() returns Party -> {
                    return partyOf(
                        mapOf(Pair("email", setOf("bob@nd.tech"))),
                        mapOf<Text, Set<Text>>()
                    );
                };
                
                @test
                function test100(test: Test) -> {
                   var alice = getAliceParty();
                   var bob = getBobParty();
                   
                   var loanRequest = LoanRequest[bob, alice]("$topic", 1000, 100);
                  
                   storeProtocol(test, "loanRequestKotlinV1", loanRequest);
                   test.assertEquals("my loan request", loanRequest.topic);
                };
            """.trimIndent(),
        )

        engine.runTo(toVersion)

        engine.runNPL(
            name = testCase.name.testName.plus("-$toVersion"),
            snippet = """
                package test;
                use crowdLending_solution05.LoanRequest;
                
                @test
                function test101(test: Test) -> {
                   var loanRequestV2 = loadProtocol<LoanRequest>(test, "loanRequestKotlinV1").getOrFail();
                   test.assertEquals("$topic", loanRequestV2.topic.title);
                };
            """.trimIndent(),
        )
    }
})
