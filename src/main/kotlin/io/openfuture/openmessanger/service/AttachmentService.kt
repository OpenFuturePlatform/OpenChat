package io.openfuture.openmessanger.service

import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import kotlin.Throws

interface AttachmentService {
    @Throws(IOException::class)
    fun upload(file: MultipartFile)

    @Throws(IOException::class)
    fun uploadAndReturnId(file: MultipartFile): Int

    @Throws(IOException::class)
    fun download(fileName: String?): ByteArray?
}