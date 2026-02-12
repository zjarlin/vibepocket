package site.addzero.vibepocket.codegen

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated

class OpenApiCodegenProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    companion object {
        private const val OPTION_SPEC = "openapi.spec"
        private const val OPTION_PACKAGE = "openapi.package"
        private const val DEFAULT_PACKAGE = "generated.api"
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val specPath = options[OPTION_SPEC]
        if (specPath.isNullOrBlank()) {
            logger.error("Missing required KSP argument: openapi.spec")
            return emptyList()
        }

        val packageName = options[OPTION_PACKAGE]?.takeIf { it.isNotBlank() } ?: DEFAULT_PACKAGE
        logger.info("OpenAPI codegen: spec=$specPath, package=$packageName")

        // TODO: Task 8.1 — wire SchemaParser → CodeEmitter pipeline here
        return emptyList()
    }
}
