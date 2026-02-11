package site.addzero.vibepocket.settings

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Property-based tests for [ConfigStore].
 *
 * // Feature: vibepocket-ui-overhaul, Property 6: 配置存储往返一致性
 * // Feature: vibepocket-ui-overhaul, Property 7: 无效 JSON 回退到默认配置
 *
 * **Validates: Requirements 5.6, 6.5**
 *
 * These tests use real file I/O via platform-specific implementations.
 * Each iteration uses a unique temp file path to avoid conflicts.
 */
@OptIn(ExperimentalKotest::class)
class ConfigStorePropertyTest {

    // ─── Generators ───────────────────────────────────────────────────────

    /**
     * Generator for random [ApiConfig] instances.
     * Generates strings of varying lengths for key, baseUrl, and label.
     */
    private val arbApiConfig: Arb<ApiConfig> = arbitrary {
        ApiConfig(
            key = Arb.string(minSize = 0, maxSize = 80).bind(),
            baseUrl = Arb.string(minSize = 0, maxSize = 80).bind(),
            label = Arb.string(minSize = 0, maxSize = 50).bind()
        )
    }

    /**
     * Generator for random [ModuleConfigs] instances.
     * Each module gets a random-length list of ApiConfig (0..5 items).
     */
    private val arbModuleConfigs: Arb<ModuleConfigs> = arbitrary {
        val musicSize = Arb.int(0..5).bind()
        val imageSize = Arb.int(0..5).bind()
        val videoSize = Arb.int(0..5).bind()

        ModuleConfigs(
            music = List(musicSize) { arbApiConfig.bind() },
            image = List(imageSize) { arbApiConfig.bind() },
            video = List(videoSize) { arbApiConfig.bind() }
        )
    }

    /**
     * Generator for strings that are NOT valid JSON for [ModuleConfigs].
     *
     * Produces a mix of:
     * - Random gibberish strings
     * - Partial/broken JSON fragments
     * - Valid JSON but wrong schema (arrays, numbers, wrong keys)
     * - Empty-ish strings that are not blank (e.g., whitespace with garbage)
     */
    private val arbInvalidJson: Arb<String> = arbitrary {
        val category = Arb.int(0..6).bind()
        when (category) {
            0 -> {
                // Random gibberish (non-JSON characters)
                Arb.string(minSize = 1, maxSize = 100).bind()
            }
            1 -> {
                // Partial JSON: opening brace but no closing
                "{\"music\":[{\"key\":\"abc\""
            }
            2 -> {
                // Valid JSON but wrong schema: a JSON array instead of object
                "[1, 2, 3]"
            }
            3 -> {
                // Valid JSON but wrong schema: object with wrong field types
                """{"music": "not_a_list", "image": 42, "video": true}"""
            }
            4 -> {
                // Just a number
                Arb.int().bind().toString()
            }
            5 -> {
                // Broken JSON with random corruption
                val base = """{"music":[],"image":[],"video":[]}"""
                val pos = Arb.int(0 until base.length).bind()
                val corruptChar = Arb.string(minSize = 1, maxSize = 3).bind()
                base.substring(0, pos) + corruptChar + base.substring(pos + 1)
            }
            else -> {
                // XML-like content (definitely not JSON)
                "<config><music>test</music></config>"
            }
        }
    }

    // ─── Helper ───────────────────────────────────────────────────────────

    /**
     * Generate a unique temp file path for each test iteration.
     */
    private fun uniqueTempPath(): String {
        val id = Random.nextLong(Long.MAX_VALUE)
        return "/tmp/vibepocket-pbt-$id.json"
    }

    /**
     * Clean up a temp file after use.
     */
    private fun cleanupFile(path: String) {
        try {
            platformWriteFile(path, "") // overwrite
            // Use platformReadFile to verify it exists, then we can't delete from common code
            // but overwriting with empty is sufficient for test isolation
        } catch (_: Exception) {
            // Ignore cleanup errors
        }
    }

    // ─── Property 6: 配置存储往返一致性 ──────────────────────────────────────

    /**
     * Property 6: 配置存储往返一致性
     *
     * For any valid ModuleConfigs object, calling ConfigStore.save(configs)
     * followed by ConfigStore.load() SHALL return a ModuleConfigs equivalent
     * to the original.
     *
     * // Feature: vibepocket-ui-overhaul, Property 6: 配置存储往返一致性
     * **Validates: Requirements 5.6**
     */
    @Test
    fun property6_configStoreRoundTrip() = runTest {
        checkAll(PropTestConfig(iterations = 5), arbModuleConfigs) { configs ->
            val path = uniqueTempPath()
            try {
                val store = ConfigStore(path)
                store.save(configs)
                val loaded = store.load()
                assertEquals(configs, loaded,
                    "Round-trip failed: saved $configs but loaded $loaded")
            } finally {
                cleanupFile(path)
            }
        }
    }

    // ─── Property 7: 无效 JSON 回退到默认配置 ────────────────────────────────

    /**
     * Property 7: 无效 JSON 回退到默认配置
     *
     * For any string that is not valid JSON (or valid JSON that does not match
     * the ModuleConfigs schema), ConfigStore.load() SHALL return the default
     * ModuleConfigs without throwing an exception.
     *
     * // Feature: vibepocket-ui-overhaul, Property 7: 无效 JSON 回退到默认配置
     * **Validates: Requirements 6.5**
     */
    @Test
    fun property7_invalidJsonFallsBackToDefault() = runTest {
        val defaultConfigs = ModuleConfigs()

        checkAll(PropTestConfig(iterations = 5), arbInvalidJson) { invalidContent ->
            val path = uniqueTempPath()
            try {
                // Write invalid JSON content directly to the file
                platformWriteFile(path, invalidContent)

                // Create a ConfigStore pointing to that file and load
                val store = ConfigStore(path)
                val loaded = store.load()

                assertEquals(defaultConfigs, loaded,
                    "Expected default ModuleConfigs for invalid JSON input: '$invalidContent', but got: $loaded")
            } finally {
                cleanupFile(path)
            }
        }
    }
}
