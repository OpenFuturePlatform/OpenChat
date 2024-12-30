package io.openfuture.openmessenger.service

interface RecordingManagementService {
    fun list()
    fun uploadToS3(fileUri: String): Int
}