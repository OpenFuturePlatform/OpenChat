package io.openfuture.openmessenger.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils
import io.openfuture.openmessenger.configuration.AwsConfig
import io.openfuture.openmessenger.repository.AttachmentRepository
import io.openfuture.openmessenger.service.AttachmentService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream

@Service
class AttachmentServiceImpl(
    val amazonS3: AmazonS3,
    val awsConfig: AwsConfig,
    val attachmentRepository: AttachmentRepository
) : AttachmentService {

    @Throws(IOException::class)
    override fun upload(file: MultipartFile) {
        val data = ObjectMetadata()
        data.contentType = file.contentType
        data.contentLength = file.size
        amazonS3.putObject(awsConfig.attachmentsBucket, file.originalFilename, file.inputStream, data)
        attachmentRepository.save(file.originalFilename)
    }

    override fun upload(name: String, fileInputStream: InputStream) {
        val data = ObjectMetadata()
        amazonS3.putObject(awsConfig.attachmentsBucket, name, fileInputStream, data)
    }

    override fun uploadAndReturnId(file: MultipartFile): Int {
        val data = ObjectMetadata()
        data.contentType = file.contentType
        data.contentLength = file.size
        amazonS3.putObject(awsConfig.attachmentsBucket, file.originalFilename, file.inputStream, data)
        val id: Int? = attachmentRepository.save(file.originalFilename)
        return id!!
    }

    @Throws(IOException::class)
    override fun download(fileName: String?, bucket: String): ByteArray? {
        val o = amazonS3.getObject(bucket, fileName)
        val s3is = o.objectContent
        return IOUtils.toByteArray(s3is)
    }

    @Throws(IOException::class)
    override fun downloadById(id: Int): ByteArray? {
        val attachmentResponses = attachmentRepository.get(listOf(id))
        val fileName = attachmentResponses?.get(0)?.name
        val o = amazonS3.getObject(awsConfig.attachmentsBucket, fileName)
        val s3is = o.objectContent
        return IOUtils.toByteArray(s3is)
    }

}