package crowdLending.config

import seed.config.IConfiguration

interface ICrowdLendingConfiguration : IConfiguration {
    val systemUser: String
    val systemPassword: String
}

data class CrowdLendingConfiguration(
    override val systemUser: String = System.getenv("SYSTEM_USER") ?: "payee1",
    override val systemPassword: String = System.getenv("SYSTEM_PASSWORD") ?: "welcome",

    private val baseConfig: IConfiguration,
) : ICrowdLendingConfiguration, IConfiguration by baseConfig
