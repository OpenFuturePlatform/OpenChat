package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.service.RecordingManagementService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/api/v1/recordings")
@RestController
class RecordingController(
    val recordingManagementService: RecordingManagementService
) {

    @GetMapping("/list")
    fun generateNotes() {
        recordingManagementService.list()
    }

}
