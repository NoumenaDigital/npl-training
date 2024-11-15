package main

import com.noumenadigital.platform.client.engine.ApplicationHttpClient
import crowdLending.config.CrowdLendingConfiguration
import crowdLending.config.ICrowdLendingConfiguration
import crowdLending.keycloak.KeycloakUserProvider
import crowdLending.sse.StreamListener
import io.prometheus.client.hotspot.DefaultExports
import iou.http.Gen
import iou.http.iouRoutes
import iou.sse.sseRoutes
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.http4k.client.ApacheClient
import org.http4k.core.then
import org.http4k.routing.routes
import org.http4k.server.KtorCIO
import org.http4k.server.PolyHandler
import org.http4k.server.Undertow
import org.http4k.server.asServer
import seed.config.Configuration
import seed.filter.accessLogFilter
import seed.filter.corsFilter
import seed.filter.errorFilter
import seed.filter.loginRequired
import seed.keycloak.KeycloakClient
import seed.keycloak.KeycloakClientImpl
import seed.keycloak.KeycloakForwardAuthorization
import seed.metrics.measure
import seed.security.AuthHandler
import seed.security.JsonKeycloakAuthHandler
import seed.server.admin
import seed.server.loginRoutes
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(): Unit = runBlocking {
    logger.info { "Starting Library-API" }

    DefaultExports.initialize()

    val httpPort = (System.getenv("HTTP_PORT") ?: "8080").toInt()
    val adminPort = (System.getenv("HTTP_ADMIN_PORT") ?: "8000").toInt()
    val config: ICrowdLendingConfiguration = CrowdLendingConfiguration(
        baseConfig = Configuration(
            keycloakRealm = System.getenv("KEYCLOAK_REALM") ?: "library",
            keycloakClientId = System.getenv("KEYCLOAK_CLIENT_ID") ?: "library",
        ),
    )

    val adminServer = admin(config).asServer(KtorCIO(adminPort))
    adminServer.start()
    val engineClient = ApplicationHttpClient(config.engineURL)
    val keycloakClient: KeycloakClient = KeycloakClientImpl(config, ApacheClient())
    val forwardAuthorization = KeycloakForwardAuthorization(keycloakClient)
    val authHandler: AuthHandler = JsonKeycloakAuthHandler(config)

    val individuallyDecoratedRoutes =
        routes(
            loginRoutes(config, authHandler),

            loginRequired(config)
                .then(iouRoutes(Gen(engineClient, forwardAuthorization))),
        )

    val globallyDecoratedRoutes =
        measure()
            .then(accessLogFilter(config))
            .then(corsFilter(config))
            .then(errorFilter(true))
            .then(individuallyDecoratedRoutes)

    val routingSseHandler = sseRoutes(engineClient, forwardAuthorization)

    val requestHandler = PolyHandler(
        http = globallyDecoratedRoutes,
        sse = routingSseHandler,
    )

    val appServer = requestHandler.asServer(Undertow(httpPort))

    val auth = KeycloakUserProvider(keycloakClient, config.systemUser, config.systemPassword)
    StreamListener(engineClient, auth, "sseListener").collect()

    logger.info { "Request logging verbosity is ${config.accessLogVerbosity}" }
    exitProcess(
        try {
            appServer.start().block()
            0
        } catch (e: Throwable) {
            logger.error(e) {}
            1
        } finally {
            adminServer.stop()

            engineClient.close()
        },
    )
}
