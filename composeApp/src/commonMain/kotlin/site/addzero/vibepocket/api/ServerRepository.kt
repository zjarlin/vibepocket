package site.addzero.vibepocket.api

import site.addzero.vibepocket.model.MusicHistorySaveRequest
import site.addzero.vibepocket.model.PersonaItem

interface ServerRepository {
    suspend fun getPersonas(): List<PersonaItem>
    suspend fun saveHistory(request: MusicHistorySaveRequest)
}
