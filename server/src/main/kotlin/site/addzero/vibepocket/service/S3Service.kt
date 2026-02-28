package site.addzero.vibepocket.service

import org.koin.core.annotation.Single
import site.addzero.vibepocket.model.S3Config
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import java.io.InputStream
import java.nio.file.Path

@Single
class S3Service(
    private val s3Client: S3Client,
    private val config: S3Config
) {

    /**
     * 上传文件
     */
    fun upload(key: String, inputStream: InputStream, contentLength: Long, contentType: String? = null): PutObjectResponse {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(config.bucket)
            .key(key)
            .contentType(contentType)
            .build()
        
        return s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength))
    }

    /**
     * 上传字节数组
     */
    fun upload(key: String, bytes: ByteArray, contentType: String? = null): PutObjectResponse {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(config.bucket)
            .key(key)
            .contentType(contentType)
            .build()
        
        return s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes))
    }

    /**
     * 下载文件
     */
    fun download(key: String): InputStream {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(config.bucket)
            .key(key)
            .build()
        
        return s3Client.getObject(getObjectRequest)
    }

    /**
     * 删除文件
     */
    fun delete(key: String): DeleteObjectResponse {
        val deleteObjectRequest = DeleteObjectRequest.builder()
            .bucket(config.bucket)
            .key(key)
            .build()
        
        return s3Client.deleteObject(deleteObjectRequest)
    }

    /**
     * 列出文件
     */
    fun list(prefix: String? = null): List<S3Object> {
        val listObjectsRequest = ListObjectsV2Request.builder()
            .bucket(config.bucket)
            .prefix(prefix)
            .build()
        
        return s3Client.listObjectsV2(listObjectsRequest).contents()
    }

    /**
     * 生成公网访问 URL (如果桶是公共读或者有预签名)
     * 这里简单拼接 URL，实际情况可能需要预签名
     */
    fun getUrl(key: String): String {
        return "${config.endpoint}/${config.bucket}/$key"
    }
}
