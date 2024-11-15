package crowdLending.sse

import com.noumenadigital.npl.api.generated.crowd_lending.LockAmountFacade
import com.noumenadigital.npl.api.generated.crowd_lending.TransferAmountFacade
import com.noumenadigital.platform.client.engine.ApplicationClient
import com.noumenadigital.platform.engine.values.ClientBooleanValue
import com.noumenadigital.platform.engine.values.ClientException
import com.noumenadigital.platform.engine.values.ClientProtocolReferenceValue
import com.noumenadigital.platform.engine.values.ClientStructValue
import mu.KLogging
import seed.sse.Event
import java.util.function.Consumer

class LockAmountEventConsumer(val client: ApplicationClient) : Consumer<Event> {
    companion object : KLogging()

    override fun accept(event: Event) {
        val payload = event.flux.payload

        if (payload.name == LockAmountFacade.typeName && payload.arguments.isNotEmpty()) {
            try {
                logger.info { "Processing lock notification: $payload" }
                logger.info { payload.arguments[0] }

                /*
                 * This service mocks a banking system interface, naïvely always answering yes. Provided an actual banking service with account and cash management,
                 * an HTTP request could be made from this service and pass the response over either as positive or negative feedback.
                 */

                val lendingRequestPayload = payload.arguments[0] as ClientProtocolReferenceValue

                val notificationSuccess = ClientStructValue(
                    "/lang/core/NotifySuccess<T>",
                    mapOf(
                        "result" to ClientStructValue(
                            "/lang/collection/Pair<S, T>",
                            mapOf(
                                "first" to ClientBooleanValue(true),
                                "second" to lendingRequestPayload,
                            ),
                        ),
                    ),
                )

                if (payload.callback != null) {
                    client.selectAction(
                        payload.refId,
                        payload.callback!!,
                        listOf(
                            notificationSuccess,
                        ),
                        event.auth,
                    )
                }
            } catch (e: Throwable) {
                logger.error(e) { "Caught error in stream listener $e" }
            } catch (e: ClientException.PlatformRuntimeException) {
                logger.error(e) { "Caught error in stream listener $e" }
            }
        }
    }
}

class TransferAmountEventConsumer(val client: ApplicationClient) : Consumer<Event> {
    companion object : KLogging()

    override fun accept(event: Event) {
        val payload = event.flux.payload

        if (payload.name == TransferAmountFacade.typeName && payload.arguments.isNotEmpty()) {
            try {
                logger.info { "Processing transfer notification: $payload" }

                /*
                 * This service mocks a banking system interface, naïvely always answering yes. Provided an actual banking service with account and cash management,
                 * an HTTP request could be made from this service and pass the response over either as positive or negative feedback.
                 */

                val transferAmountPayload = payload.arguments[0] as ClientProtocolReferenceValue

                val notificationSuccess = ClientStructValue(
                    "/lang/core/NotifySuccess<T>",
                    mapOf(
                        "result" to ClientStructValue(
                            "/lang/collection/Pair<S, T>",
                            mapOf(
                                "first" to ClientBooleanValue(true),
                                "second" to transferAmountPayload,
                            ),
                        ),
                    ),
                )

                if (payload.callback != null) {
                    client.selectAction(
                        payload.refId,
                        payload.callback!!,
                        listOf(
                            notificationSuccess,
                        ),
                        event.auth,
                    )
                }
            } catch (e: Throwable) {
                logger.error(e) { "Caught error in stream listener $e" }
            } catch (e: ClientException.PlatformRuntimeException) {
                logger.error(e) { "Caught error in stream listener $e" }
            }
        }
    }
}
