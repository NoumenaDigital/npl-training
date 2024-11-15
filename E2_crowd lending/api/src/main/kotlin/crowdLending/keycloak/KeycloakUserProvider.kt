package crowdLending.keycloak

import com.noumenadigital.platform.engine.client.Authorization
import com.noumenadigital.platform.engine.client.AuthorizationProvider
import seed.keycloak.KeycloakClient

class KeycloakUserProvider(
    private val keycloakClient: KeycloakClient,
    private val username: String,
    private val password: String,
) : AuthorizationProvider {
    override fun invoke(): Authorization? {
        val kcToken = keycloakClient.login(username, password)
        return Authorization("Bearer", kcToken.accessToken, kcToken.refreshToken)
    }
}
