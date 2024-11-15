package crowdLending.sse

import com.noumenadigital.platform.client.engine.ApplicationHttpClient
import com.noumenadigital.platform.engine.client.AuthorizationProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mu.KotlinLogging
import seed.config.suspendRetry
import seed.sse.Event
import java.time.Duration

private val logger = KotlinLogging.logger {}

internal class StreamListener(
    private val engineClient: ApplicationHttpClient,
    private val auth: AuthorizationProvider,
    private val coroutineName: String,
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, e ->
        logger.error(e) { "Failure writing to logbook stream" }
    }
    private val scope = CoroutineScope(CoroutineName("sources") + exceptionHandler + Dispatchers.IO)

    fun collect() {
        scope.launch(CoroutineName(coroutineName)) {
            suspendRetry(100, Duration.ofSeconds(30)) {
                try {
                    engineClient.notifications(-1, auth).forEach { n ->
                        listOf(
                            LockAmountEventConsumer(engineClient),
                            TransferAmountEventConsumer(engineClient),
                        ).forEach { it.accept(Event(n, auth)) }
                    }
                } catch (e: Throwable) {
                    logger.error(e) { "Error in stream listener $e" }
                }
            }
        }
    }
}
