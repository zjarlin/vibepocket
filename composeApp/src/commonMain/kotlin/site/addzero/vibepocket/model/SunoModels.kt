@file:Suppress("unused")
package site.addzero.vibepocket.model

// Re-export from api-suno module for backward compatibility
typealias SunoGenerateRequest = site.addzero.vibepocket.api.suno.SunoGenerateRequest
typealias SunoExtendRequest = site.addzero.vibepocket.api.suno.SunoExtendRequest
typealias SunoUploadCoverRequest = site.addzero.vibepocket.api.suno.SunoUploadCoverRequest
typealias SunoUploadExtendRequest = site.addzero.vibepocket.api.suno.SunoUploadExtendRequest
typealias SunoAddVocalsRequest = site.addzero.vibepocket.api.suno.SunoAddVocalsRequest
typealias SunoAddInstrumentalRequest = site.addzero.vibepocket.api.suno.SunoAddInstrumentalRequest
typealias SunoLyricsRequest = site.addzero.vibepocket.api.suno.SunoLyricsRequest
typealias SunoGeneratePersonaRequest = site.addzero.vibepocket.api.suno.SunoGeneratePersonaRequest
typealias SunoTimestampedLyricsRequest = site.addzero.vibepocket.api.suno.SunoTimestampedLyricsRequest
typealias SunoVocalRemovalRequest = site.addzero.vibepocket.api.suno.SunoVocalRemovalRequest
typealias SunoBoostStyleRequest = site.addzero.vibepocket.api.suno.SunoBoostStyleRequest
typealias SunoMusicCoverRequest = site.addzero.vibepocket.api.suno.SunoMusicCoverRequest
typealias SunoReplaceSectionRequest = site.addzero.vibepocket.api.suno.SunoReplaceSectionRequest
typealias SunoWavRequest = site.addzero.vibepocket.api.suno.SunoWavRequest
typealias SunoSubmitData = site.addzero.vibepocket.api.suno.SunoSubmitData
typealias SunoTrack = site.addzero.vibepocket.api.suno.SunoTrack
typealias SunoTaskResponse = site.addzero.vibepocket.api.suno.SunoTaskResponse
typealias SunoTaskDetail = site.addzero.vibepocket.api.suno.SunoTaskDetail
typealias SunoLyricItem = site.addzero.vibepocket.api.suno.SunoLyricItem
typealias SunoLyricsResponse = site.addzero.vibepocket.api.suno.SunoLyricsResponse
typealias SunoLyricsTaskDetail = site.addzero.vibepocket.api.suno.SunoLyricsTaskDetail
typealias SunoPersonaData = site.addzero.vibepocket.api.suno.SunoPersonaData
typealias SunoAlignedWord = site.addzero.vibepocket.api.suno.SunoAlignedWord
typealias SunoTimestampedLyricsData = site.addzero.vibepocket.api.suno.SunoTimestampedLyricsData
typealias SunoCredits = site.addzero.vibepocket.api.suno.SunoCredits
typealias SunoBoostStyleData = site.addzero.vibepocket.api.suno.SunoBoostStyleData

val SUNO_MODELS = site.addzero.vibepocket.api.suno.SUNO_MODELS
val VOCAL_GENDERS = site.addzero.vibepocket.api.suno.VOCAL_GENDERS

/** Vibe 表单步骤 */
enum class VibeStep { LYRICS, PARAMS }
