package site.addzero.vibepocket.model

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import site.addzero.vibepocket.navigation.MenuMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Property-based tests for serialization round-trip consistency.
 *
 * // Feature: vibepocket-ui-overhaul, Property 1: MenuMetadata JSON 序列化往返一致性
 * **Validates: Requirements 4.2**
 */
@OptIn(ExperimentalKotest::class)
class SerializationPropertyTest {

    /**
     * Generator for random [MenuMetadata] instances.
     * - routeKey and menuNameAlias are non-empty strings (1..50 chars)
     * - icon and parentRouteKey are nullable strings
     * - visible is any boolean
     * - sortOrder is any int
     */
    private val arbMenuMetadata: Arb<MenuMetadata> = arbitrary {
        MenuMetadata(
            routeKey = Arb.string(minSize = 1, maxSize = 50).bind(),
            menuNameAlias = Arb.string(minSize = 1, maxSize = 50).bind(),
            icon = Arb.string(minSize = 1, maxSize = 20).orNull(nullProbability = 0.3).bind(),
            parentRouteKey = Arb.string(minSize = 1, maxSize = 50).orNull(nullProbability = 0.3).bind(),
            visible = Arb.boolean().bind(),
            sortOrder = Arb.int().bind()
        )
    }

    /**
     * Property 1: MenuMetadata JSON 序列化往返一致性
     *
     * For any valid MenuMetadata object, serializing it to JSON and then
     * deserializing the JSON back produces an object equivalent to the original.
     *
     * // Feature: vibepocket-ui-overhaul, Property 1: MenuMetadata JSON 序列化往返一致性
     * **Validates: Requirements 4.2**
     */
    @Test
    fun menuMetadataJsonRoundTrip() = runTest {
        checkAll(PropTestConfig(iterations = 100), arbMenuMetadata) { metadata ->
            val json = Json.encodeToString(MenuMetadata.serializer(), metadata)
            val deserialized = Json.decodeFromString(MenuMetadata.serializer(), json)
            assertEquals(metadata, deserialized)
        }
    }
}
