package io.openfuture.openmessenger.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.S3Object
import io.openfuture.openmessenger.configuration.AwsConfig
import io.openfuture.openmessenger.kurento.recording.UserSession
import io.openfuture.openmessenger.repository.AttachmentRepository
import io.openfuture.openmessenger.service.RecordingManagementService
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URLConnection


@Service
class RecordingManagementServiceImpl(
    val amazonS3: AmazonS3,
    val awsConfig: AwsConfig,
    val attachmentRepository: AttachmentRepository
): RecordingManagementService {
    override fun list() {
        val listObjectsV2Request = ListObjectsV2Request().withBucketName(awsConfig.recordingsBucket)
        val listObjectsV2Response: ListObjectsV2Result = amazonS3.listObjectsV2(listObjectsV2Request)
        println("Number of objects in the bucket: " + listObjectsV2Response.objectSummaries.size)
        listObjectsV2Response.objectSummaries.forEach {
            println("file = ${it.key}, size: ${it.lastModified}")
        }
    }

    override fun uploadToS3(fileFullPath: String): Int {
        val fileUri = fileFullPath.removePrefix("file://")
        log.info("Uploading file to S3: $fileUri")
        val file = File(fileUri)
        if (!file.exists()) {
            log.warn("File does not exist: $fileUri")
            return -1
        }
        val data = ObjectMetadata()
        val fis: InputStream = BufferedInputStream(FileInputStream(file))
        val mimeType = URLConnection.guessContentTypeFromStream(fis)
        data.contentType = mimeType
        data.contentLength = file.length()
        amazonS3.putObject(awsConfig.recordingsBucket, file.name, fis, data)
        val id: Int? = attachmentRepository.save(file.name)
        return id!!
    }

    companion object {
        private val log: Logger = getLogger(UserSession::class.java)
    }

}