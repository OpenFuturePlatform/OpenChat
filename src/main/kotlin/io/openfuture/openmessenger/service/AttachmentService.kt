package io.openfuture.openmessenger.service

import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.io.InputStream
import kotlin.Throws

interface AttachmentService {
    @Throws(IOException::class)
    fun upload(file: MultipartFile)

    fun upload(name: String, fileInputStream: InputStream)

    @Throws(IOException::class)
    fun uploadAndReturnId(file: MultipartFile): Int

    @Throws(IOException::class)
    fun download(fileName: String?, bucket: String): ByteArray?

    fun downloadById(id: Int): ByteArray?
}