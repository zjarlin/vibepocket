package site.addzero.vibepocket.di

import org.koin.dsl.module
import site.addzero.vibepocket.api.ServerRepository
import site.addzero.vibepocket.model.MusicHistorySaveRequest
import site.addzero.vibepocket.model.PersonaItem

val appModule = module {
    // Provide a temporary implementation of ServerRepository for commonMain
    single<ServerRepository> {
        object : ServerRepository {
            override suspend fun getPersonas(): List<PersonaItem> {
                println("Warning: Called dummy ServerRepository.getPersonas()")
                return emptyList()
            }

            override suspend fun saveHistory(request: MusicHistorySaveRequest) {
                println("Warning: Called dummy ServerRepository.saveHistory()")
            }
        }
    }
}
