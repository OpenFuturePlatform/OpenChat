package io.openfuture.openmessanger.web.controller

import io.openfuture.openmessanger.service.AttachmentService
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException

@RequestMapping("/api/v1/attachments")
@RestController
@RequiredArgsConstructor
class AttachmentController (
    val attachmentService: AttachmentService
){

    @PostMapping
    @Throws(IOException::class)
    fun handleFileUpload(@RequestParam("file") file: MultipartFile): String? {
        attachmentService.upload(file)
        return file.originalFilename
    }

    @PostMapping("/upload")
    @Throws(IOException::class)
    fun upload(@RequestParam("file") file: MultipartFile): Int {
        return attachmentService.uploadAndReturnId(file)
    }

    @GetMapping(value = ["/{fileName}"], produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    @Throws(IOException::class)
    fun download(response: HttpServletResponse, @PathVariable(value = "fileName") fileName: String) {
        val fileData = attachmentService.download(fileName)
        response.reset()
        response.contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment$fileName")
        response.setContentLength(fileData!!.size)
        response.outputStream.write(fileData)
        response.flushBuffer()
    }
}