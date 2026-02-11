package site.addzero.vibepocket.music

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Property-based tests for AudioPlayerManager single active player invariant.
 *
 * // Feature: suno-client-completion, Property 1: 单一活跃播放器不变量
 * **Validates: Requirements 1.4**
 *
 * Since AudioPlayerManager is a singleton backed by GadulkaPlayer (which requires
 * a real audio backend), this test validates the STATE MANAGEMENT LOGIC:
 * - After calling play(trackId, audioUrl), currentTrackId.value == trackId
 * - At any point, only the LAST played track is the current one
 * - After stop(), currentTrackId.value == null
 */
@OptIn(ExperimentalKotest::class)
class AudioPlayerManagerPropertyTest {

    /**
     * Generator for a (trackId, audioUrl) pair.
     * trackId: non-empty string (1..30 chars)
     * audioUrl: non-empty string simulating a URL (1..100 chars)
     */
    private val arbTrackPair: Arb<Pair<String, String>> = arbitrary {
        val trackId = Arb.string(minSize = 1, maxSize = 30).bind()
        val audioUrl = Arb.string(minSize = 1, maxSize = 100).bind()
        trackId to audioUrl
    }

    /**
     * Generator for a non-empty list of (trackId, audioUrl) pairs (1..10 items).
     */
    private val arbTrackPairList: Arb<List<Pair<String, String>>> =
        Arb.list(arbTrackPair, range = 1..10)

    /**
     * Property 1: 单一活跃播放器不变量
     *
     * For any sequence of play() calls with different trackIds, after each play(trackId, url)
     * call, currentTrackId.value MUST equal that trackId — proving that at most one track
     * is the "current" track at any point, and it is always the LAST one played.
     *
     * After stop(), currentTrackId.value MUST be null.
     *
     * NOTE: Because AudioPlayerManager is a singleton that internally creates a real
     * GadulkaPlayer, calling play() will attempt actual audio playback which may throw
     * on platforms without an audio backend. This test validates the invariant by calling
     * play() and immediately checking currentTrackId — the state assignment happens
     * synchronously before the async player.play() call. On platforms where GadulkaPlayer
     * initialization fails, this test should be run on a JVM/Desktop target.
     *
     * // Feature: suno-client-completion, Property 1: 单一活跃播放器不变量
     * **Validates: Requirements 1.4**
     */
    @Test
    fun singleActivePlayerInvariant() = runTest {
        checkAll(PropTestConfig(iterations = 100), arbTrackPairList) { trackPairs ->
            for ((trackId, audioUrl) in trackPairs) {
                try {
                    AudioPlayerManager.play(trackId, audioUrl)
                } catch (_: Exception) {
                    // GadulkaPlayer.play() may throw on test platforms without audio backend.
                    // The state assignment (currentTrackId = trackId) happens BEFORE player.play(),
                    // so the invariant still holds.
                }

                // Invariant: currentTrackId must be the LAST played trackId
                assertEquals(
                    trackId,
                    AudioPlayerManager.currentTrackId.value,
                    "After play($trackId, ...), currentTrackId should be $trackId"
                )
            }

            // After stop(), no track should be active
            AudioPlayerManager.stop()
            assertEquals(
                null,
                AudioPlayerManager.currentTrackId.value,
                "After stop(), currentTrackId should be null"
            )
        }
    }

    /**
     * Property 2: 播放/暂停状态切换
     *
     * For any (trackId, audioUrl) pair, the state transitions are deterministic:
     *   play(trackId, audioUrl) → state is BUFFERING (synchronous assignment)
     *   pause()                 → state is PAUSED
     *   resume()                → state is PLAYING (synchronous assignment)
     *   stop()                  → state is IDLE (cleanup)
     *
     * // Feature: suno-client-completion, Property 2: 播放/暂停状态切换
     * **Validates: Requirements 1.2, 1.3**
     */
    @Test
    fun playPauseStateToggle() = runTest {
        checkAll(PropTestConfig(iterations = 100), arbTrackPair) { (trackId, audioUrl) ->
            // Ensure clean state
            AudioPlayerManager.stop()
            assertEquals(PlayerState.IDLE, AudioPlayerManager.playerState.value)

            // play() → state should be BUFFERING (set synchronously before player.play())
            try {
                AudioPlayerManager.play(trackId, audioUrl)
            } catch (_: Exception) {
                // Audio backend may not be available in test
            }
            assertEquals(
                PlayerState.BUFFERING,
                AudioPlayerManager.playerState.value,
                "After play($trackId, ...), playerState should be BUFFERING"
            )

            // pause() → state should be PAUSED (BUFFERING is a valid state for pause)
            AudioPlayerManager.pause()
            assertEquals(
                PlayerState.PAUSED,
                AudioPlayerManager.playerState.value,
                "After pause(), playerState should be PAUSED"
            )

            // resume() → state should be PLAYING (set synchronously in resume())
            try {
                AudioPlayerManager.resume()
            } catch (_: Exception) {
                // Audio backend may not be available in test
            }
            assertEquals(
                PlayerState.PLAYING,
                AudioPlayerManager.playerState.value,
                "After resume(), playerState should be PLAYING"
            )

            // Clean up
            AudioPlayerManager.stop()
            assertEquals(
                PlayerState.IDLE,
                AudioPlayerManager.playerState.value,
                "After stop(), playerState should be IDLE"
            )
        }
    }

    /**
     * Supplementary check: for any single play() call, the playerState should not remain IDLE
     * (it transitions to BUFFERING on play). After stop(), it returns to IDLE.
     *
     * // Feature: suno-client-completion, Property 1: 单一活跃播放器不变量
     * **Validates: Requirements 1.4**
     */
    @Test
    fun playTransitionsFromIdle() = runTest {
        checkAll(PropTestConfig(iterations = 100), arbTrackPair) { (trackId, audioUrl) ->
            // Ensure clean state
            AudioPlayerManager.stop()
            assertEquals(PlayerState.IDLE, AudioPlayerManager.playerState.value)

            try {
                AudioPlayerManager.play(trackId, audioUrl)
            } catch (_: Exception) {
                // Audio backend may not be available in test
            }

            // After play(), state should have transitioned away from IDLE
            // (it's set to BUFFERING synchronously in play())
            assertTrue(
                AudioPlayerManager.playerState.value != PlayerState.IDLE,
                "After play(), playerState should not be IDLE (expected BUFFERING or later state)"
            )

            // Clean up
            AudioPlayerManager.stop()
            assertEquals(PlayerState.IDLE, AudioPlayerManager.playerState.value)
        }
    }
}
