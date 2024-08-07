package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.service.AttachmentService
import io.openfuture.openmessenger.service.VideoService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream

@RestController
@RequestMapping("/api/v1/utils")
class UtilsController(
    val videoService: VideoService,
    val attachmentService: AttachmentService
) {

    @PostMapping("extractAudio")
    fun sparks() {
        val fileInputStream = FileInputStream(File("audiofiles/meeting.mp3"))
        attachmentService.upload("meeting-11-07-2024.mp3", fileInputStream)
    }
//        videoService.convertToAudio()

}