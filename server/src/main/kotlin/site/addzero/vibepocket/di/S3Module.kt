package site.addzero.vibepocket.di

import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import site.addzero.vibepocket.model.S3Config
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Module
@Configuration
class S3Module {

    @Single
    fun s3Config(): S3Config {
        val configUrl = object {}.javaClass.getResource("/application.yml")
            ?: return S3Config()

        val yamlContent = configUrl.readText()

        val endpoint = yamlContent.substringAfter("s3:").substringAfter("endpoint:").substringBefore("\n").trim().trim('"', '\'')
        val region = yamlContent.substringAfter("s3:").substringAfter("region:").substringBefore("\n").trim().trim('"', '\'')
        val bucket = yamlContent.substringAfter("s3:").substringAfter("bucket:").substringBefore("\n").trim().trim('"', '\'')
        val accessKey = yamlContent.substringAfter("s3:").substringAfter("accessKey:").substringBefore("\n").trim().trim('"', '\'')
        val secretKey = yamlContent.substringAfter("s3:").substringAfter("secretKey:").substringBefore("\n").trim().trim('"', '\'')

        return S3Config(
            endpoint = if (endpoint.isEmpty()) "https://s3.cstcloud.cn" else endpoint,
            region = if (region.isEmpty()) "us-east-1" else region,
            bucket = if (bucket.isEmpty()) "af466fd92b0146ccbfb40cf590c912a0" else bucket,
            accessKey = accessKey,
            secretKey = secretKey
        )
    }

    @Single
    fun s3Client(config: S3Config): S3Client {
        val credentials = AwsBasicCredentials.create(config.accessKey, config.secretKey)
        return S3Client.builder()
            .endpointOverride(URI.create(config.endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(config.region))
            .forcePathStyle(true) // Most S3-compatible services use path style
            .build()
    }
}
