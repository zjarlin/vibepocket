package site.addzero.vibepocket.music.suno

import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Property
import site.addzero.vibepocket.api.suno.SunoApiClient

// This module will be processed by Koin KSP to generate Koin definitions.
// Make sure you have koin-ksp properly configured in your build.gradle.kts
@Module
class SunoApiBindings {

    // Registers SunoApiClient as a factory.
    // A new instance of SunoApiClient is created each time it's requested.
    // It retrieves 'token' and 'baseUrl' from Koin's properties.
    @Factory
    fun provideSunoApiClient(
        @Property("suno.apiToken") token: String,
    ): SunoApiClient {
        return SunoApiClient(apiToken = token)
    }
}
