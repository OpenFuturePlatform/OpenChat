package io.openfuture.openmessanger.service.impl

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.util.IOUtils
import io.openfuture.openmessanger.configuration.AwsConfig
import io.openfuture.openmessanger.repository.AttachmentRepository
import io.openfuture.openmessanger.service.AttachmentService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

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

    override fun uploadAndReturnId(file: MultipartFile): Int {
        val data = ObjectMetadata()
        data.contentType = file.contentType
        data.contentLength = file.size
        amazonS3.putObject(awsConfig.attachmentsBucket, file.originalFilename, file.inputStream, data)
        val id: Int? = attachmentRepository.save(file.originalFilename)
        return id!!
    }

    @Throws(IOException::class)
    override fun download(fileName: String?): ByteArray? {
        val o = amazonS3.getObject(awsConfig.attachmentsBucket, fileName)
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